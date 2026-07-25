package com.tarxs.entranex.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarxs.entranex.data.local.TokenStore
import com.tarxs.entranex.data.network.LoginRequest
import com.tarxs.entranex.data.network.RetrofitClient
import com.tarxs.entranex.data.network.SignupRequest
import kotlinx.coroutines.launch
import java.io.IOException

enum class AuthTab { LOGIN, SIGNUP }

enum class PasswordStrength(val label: String) {
    NONE(""), WEAK("Weak"), MEDIUM("Medium"), STRONG("Strong")
}

class AuthViewModel(private val tokenStore: TokenStore) : ViewModel() {

    private val api = RetrofitClient.authApi(tokenStore)

    // ── Tab state ──
    var currentTab by mutableStateOf(AuthTab.LOGIN)
        private set

    fun switchTab(tab: AuthTab) {
        currentTab = tab
        loginError = null
        signupError = null
        signupSuccess = null
    }

    // ── Login fields ──
    var loginIdentifier by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginLoading by mutableStateOf(false)
        private set
    var loginError by mutableStateOf<String?>(null)
        private set

    // ── Signup fields ──
    var signupUsername by mutableStateOf("")
    var signupEmail by mutableStateOf("")
    var signupPassword by mutableStateOf("")
    var signupPasswordConfirm by mutableStateOf("")
    var signupLoading by mutableStateOf(false)
        private set
    var signupError by mutableStateOf<String?>(null)
        private set
    var signupSuccess by mutableStateOf<String?>(null)
        private set

    // ── Result: emits true once login succeeds and token is stored ──
    var loginSucceeded by mutableStateOf(false)
        private set

    /**
     * Mirrors computePasswordStrength(pw) from index.html:
     *   +1 length >= 8, +1 length >= 12, +1 has upper&lower,
     *   +1 has digit, +1 has special char.
     *   <=1 Weak, <=3 Medium, else Strong.
     */
    fun passwordStrength(pw: String): PasswordStrength {
        if (pw.isEmpty()) return PasswordStrength.NONE
        var score = 0
        if (pw.length >= 8) score++
        if (pw.length >= 12) score++
        if (pw.any { it.isUpperCase() } && pw.any { it.isLowerCase() }) score++
        if (pw.any { it.isDigit() }) score++
        if (pw.any { !it.isLetterOrDigit() }) score++
        return when {
            score <= 1 -> PasswordStrength.WEAK
            score <= 3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.STRONG
        }
    }

    /** Mirrors ennexLogin() */
    fun login(onSuccess: () -> Unit) {
        loginError = null
        loginLoading = true
        viewModelScope.launch {
            try {
                val response = api.login(
                    LoginRequest(
                        identifier = loginIdentifier.trim(),
                        password = loginPassword
                    )
                )
                val body = response.body()
                if (response.isSuccessful && body?.accessToken != null) {
                    tokenStore.token = body.accessToken
                    body.user?.username?.let { tokenStore.userHandle = it }
                    loginSucceeded = true
                    onSuccess()
                } else {
                    loginError = body?.detail ?: "Login failed (${response.code()})"
                }
            } catch (e: IOException) {
                loginError = "Network error — the API may be waking up, try again in 30s"
            } catch (e: Exception) {
                loginError = e.message ?: "Something went wrong"
            } finally {
                loginLoading = false
            }
        }
    }

    /** Mirrors ennexSignup(), including the same client-side validation */
    fun signup(onSwitchToLogin: () -> Unit) {
        signupError = null
        signupSuccess = null

        if (signupPassword.length < 8) {
            signupError = "Password must be at least 8 characters."
            return
        }
        if (signupPassword != signupPasswordConfirm) {
            signupError = "Passwords do not match."
            return
        }

        signupLoading = true
        viewModelScope.launch {
            try {
                val response = api.signup(
                    SignupRequest(
                        username = signupUsername.trim(),
                        email = signupEmail.trim(),
                        password = signupPassword
                    )
                )
                val body = response.body()
                if (response.isSuccessful) {
                    signupSuccess = body?.message ?: "Account created — you can log in now."
                    loginIdentifier = signupUsername.trim()
                    onSwitchToLogin()
                } else {
                    signupError = body?.detail ?: "Signup failed (${response.code()})"
                }
            } catch (e: IOException) {
                signupError = "Network error — the API may be waking up, try again in 30s"
            } catch (e: Exception) {
                signupError = e.message ?: "Something went wrong"
            } finally {
                signupLoading = false
            }
        }
    }
}
