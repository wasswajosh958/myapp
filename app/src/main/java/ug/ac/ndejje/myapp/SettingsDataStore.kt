package ug.ac.ndejje.myapp

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CURRENCY = stringPreferencesKey("currency")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }
    
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_MODE]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val accentColorFlow: Flow<AccentColor> = context.dataStore.data.map { prefs ->
        when (prefs[ACCENT_COLOR]) {
            "BLUE" -> AccentColor.BLUE
            "PURPLE" -> AccentColor.PURPLE
            else -> AccentColor.GREEN
        }
    }

    val currencyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENCY] ?: "Shs"
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[THEME_MODE] = mode.name }
    }

    suspend fun setAccentColor(color: AccentColor) {
        context.dataStore.edit { prefs -> prefs[ACCENT_COLOR] = color.name }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { prefs -> prefs[CURRENCY] = currency }
    }
}
