package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
      application: Application,
      private val sharingInteractor: SharingInteractor,
      private val settingsInteractor: SettingsInteractor

) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(application: Application, sharingInteractor: SharingInteractor  ,settingsInteractor: SettingsInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(application, sharingInteractor, settingsInteractor, )
            }
        }
    }

    private val _themeSettings = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> get() = _themeSettings

    private fun loadThemeSettings() {
        _themeSettings.value = settingsInteractor.getThemeSettings()
    }

    fun updateThemeSettings(isDarkTheme: Boolean) {
        val newThemeSettings = ThemeSettings(isDarkTheme)
        settingsInteractor.updateThemeSetting(newThemeSettings)
        _themeSettings.value = newThemeSettings
        (getApplication() as App).switchTheme(isDarkTheme)
    }

    init {
        loadThemeSettings()
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