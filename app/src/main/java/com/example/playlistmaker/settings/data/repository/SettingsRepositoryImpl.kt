package com.example.playlistmaker.settings.data.repository

import android.content.Context
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsRepository

private const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preference"
private const val DARK_THEME_ENABLED_KEY = "dark_theme_enabled_key"

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {

    private val sharedPreferences =
        context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)

    override fun getThemeSettings(): ThemeSettings {
        val isDarkEnabled = sharedPreferences.getBoolean(DARK_THEME_ENABLED_KEY, false)
        return ThemeSettings(isDarkEnabled)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit().putBoolean(DARK_THEME_ENABLED_KEY, settings.isDarkThemeEnabled)
            .apply()
    }

}