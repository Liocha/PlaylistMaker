package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor

) : AndroidViewModel(application) {

    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> get() = _themeSettings

    init {
        loadThemeSettings()
    }

    private fun loadThemeSettings() {
        _themeSettings.value = settingsInteractor.getThemeSettings()
    }


    fun updateThemeSettings(isDarkTheme: Boolean) {
        val newThemeSettings = ThemeSettings(isDarkTheme)
        settingsInteractor.updateThemeSetting(newThemeSettings)
        _themeSettings.value = newThemeSettings
        (getApplication() as App).switchTheme(isDarkTheme)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun writeToSupport() {
        sharingInteractor.openSupport()
    }

    fun termsOfUse() {
        sharingInteractor.openTerms()
    }


}