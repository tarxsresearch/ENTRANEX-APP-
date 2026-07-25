package com.tarxs.entranex.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarxs.entranex.ui.theme.*

/**
 * Native equivalent of #auth-screen / .auth-box from index.html.
 * Two tabs (Sign Up default-active, Log In), glass card styling.
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(GlassBg, RoundedCornerShape(24.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Entranex",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "TARXS Industries",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "SIGN IN TO CONTINUE",
                color = TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(20.dp))

            // ── Tabs ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassBg, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                AuthTabButton(
                    label = "Sign Up",
                    selected = viewModel.currentTab == AuthTab.SIGNUP,
                    onClick = { viewModel.switchTab(AuthTab.SIGNUP) },
                    modifier = Modifier.weight(1f)
                )
                AuthTabButton(
                    label = "Log In",
                    selected = viewModel.currentTab == AuthTab.LOGIN,
                    onClick = { viewModel.switchTab(AuthTab.LOGIN) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            if (viewModel.currentTab == AuthTab.SIGNUP) {
                SignupPanel(viewModel, onSwitchedToLogin = { /* tab already switches */ })
            } else {
                LoginPanel(viewModel, onAuthenticated = onAuthenticated)
            }
        }
    }
}

@Composable
private fun AuthTabButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) GlassHighlight else androidx.compose.ui.graphics.Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) TextPrimary else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SignupPanel(viewModel: AuthViewModel, onSwitchedToLogin: () -> Unit) {
    val strength = viewModel.passwordStrength(viewModel.signupPassword)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AuthTextField(
            value = viewModel.signupUsername,
            onValueChange = { viewModel.signupUsername = it },
            placeholder = "Username"
        )
        AuthTextField(
            value = viewModel.signupEmail,
            onValueChange = { viewModel.signupEmail = it },
            placeholder = "Email",
            keyboardType = KeyboardType.Email
        )
        AuthTextField(
            value = viewModel.signupPassword,
            onValueChange = { viewModel.signupPassword = it },
            placeholder = "Password (min 8 chars)",
            isPassword = true
        )

        if (viewModel.signupPassword.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = {
                        when (strength) {
                            PasswordStrength.WEAK -> 0.33f
                            PasswordStrength.MEDIUM -> 0.66f
                            PasswordStrength.STRONG -> 1f
                            PasswordStrength.NONE -> 0f
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    color = when (strength) {
                        PasswordStrength.WEAK -> DangerRed
                        PasswordStrength.MEDIUM -> AccentOrange
                        PasswordStrength.STRONG -> SuccessGreen
                        PasswordStrength.NONE -> TextSecondary
                    }
                )
                Text(strength.label, color = TextSecondary, fontSize = 11.sp)
            }
        }

        AuthTextField(
            value = viewModel.signupPasswordConfirm,
            onValueChange = { viewModel.signupPasswordConfirm = it },
            placeholder = "Confirm Password",
            isPassword = true
        )

        if (viewModel.signupPasswordConfirm.isNotEmpty() &&
            viewModel.signupPasswordConfirm != viewModel.signupPassword
        ) {
            Text("Passwords do not match", color = DangerRed, fontSize = 12.sp)
        }

        Button(
            onClick = { viewModel.signup(onSwitchedToLogin) },
            enabled = !viewModel.signupLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
        ) {
            Text(if (viewModel.signupLoading) "Creating..." else "Create Account")
        }

        viewModel.signupError?.let {
            Text(it, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
        viewModel.signupSuccess?.let {
            Text(it, color = SuccessGreen, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun LoginPanel(viewModel: AuthViewModel, onAuthenticated: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AuthTextField(
            value = viewModel.loginIdentifier,
            onValueChange = { viewModel.loginIdentifier = it },
            placeholder = "Username or Email"
        )
        AuthTextField(
            value = viewModel.loginPassword,
            onValueChange = { viewModel.loginPassword = it },
            placeholder = "Password",
            isPassword = true
        )

        Button(
            onClick = { viewModel.login(onAuthenticated) },
            enabled = !viewModel.loginLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
        ) {
            Text(if (viewModel.loginLoading) "Logging in..." else "Log In")
        }

        viewModel.loginError?.let {
            Text(it, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextSecondary) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = AccentCyan,
            unfocusedBorderColor = GlassBorder,
            cursorColor = AccentCyan
        )
    )
}
