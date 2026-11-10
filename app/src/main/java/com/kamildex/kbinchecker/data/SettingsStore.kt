package com.kamildex.kbinchecker.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val saveHistory: Boolean = true
)

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(read())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    private fun read(): Settings {
        val index = prefs.getInt(KEY_THEME, 0)
        val mode = ThemeMode.entries.getOrElse(index) { ThemeMode.SYSTEM }
        return Settings(themeMode = mode, saveHistory = prefs.getBoolean(KEY_SAVE_HISTORY, true))
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putInt(KEY_THEME, mode.ordinal).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setSaveHistory(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SAVE_HISTORY, enabled).apply()
        _settings.value = _settings.value.copy(saveHistory = enabled)
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
        const val KEY_SAVE_HISTORY = "save_history"
    }
}
