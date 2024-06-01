package com.example.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

private const val DARK_THEME_ENABLED_KEY = "dark_theme_enabled_key"

class SettingsRepositoryImpl(private val prefs: SharedPreferences) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val isDarkEnabled = prefs.getBoolean(DARK_THEME_ENABLED_KEY, false)
        return ThemeSettings(isDarkEnabled)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        prefs.edit().putBoolean(DARK_THEME_ENABLED_KEY, settings.isDarkThemeEnabled)
            .apply()
    }

}