package com.tarxs.entranex.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure, on-device token storage.
 *
 * This is the native equivalent of the HTML app's:
 *   localStorage.getItem('ennexToken')
 *   localStorage.setItem('ennexToken', ...)
 *   localStorage.getItem('ennexUserHandle')
 *
 * Unlike localStorage (plain text, readable by any injected script in a
 * WebView), this is backed by the Android Keystore — the JWT is encrypted
 * at rest using a hardware-backed key when the device supports it.
 */
class TokenStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "entranex_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var userHandle: String?
        get() = prefs.getString(KEY_HANDLE, null)
        set(value) = prefs.edit().putString(KEY_HANDLE, value).apply()

    fun clear() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_HANDLE).apply()
    }

    companion object {
        private const val KEY_TOKEN = "ennex_token"
        private const val KEY_HANDLE = "ennex_user_handle"
    }
}
