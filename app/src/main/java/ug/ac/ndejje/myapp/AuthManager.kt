package ug.ac.ndejje.myapp

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_auth",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setCurrentUser(userId: Int, username: String) {
        prefs.edit()
            .putInt("current_user_id", userId)
            .putString("current_username", username)
            .putBoolean("is_logged_in", true)
            .apply()
    }

    fun getCurrentUserId(): Int = prefs.getInt("current_user_id", -1)
    fun getCurrentUsername(): String = prefs.getString("current_username", "") ?: ""

    fun setPin(pin: String) {
        prefs.edit().putString("user_pin", pin).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val savedPin = prefs.getString("user_pin", null)
        return savedPin != null && savedPin == pin
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (verifyPin(oldPin)) {
            setPin(newPin)
            return true
        }
        return false
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun clearSession() {
        prefs.edit()
            .remove("is_logged_in")
            .remove("current_user_id")
            .remove("current_username")
            .apply()
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean("biometric_enabled", false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean("onboarding_completed", false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }
}
