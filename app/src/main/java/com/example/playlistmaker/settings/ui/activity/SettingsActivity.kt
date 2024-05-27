package com.example.playlistmaker.settings.ui.activity

import android.app.Application
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val application = applicationContext as Application
        val sharingInteractor = Creator.provideSharingInteractor(this)
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val factory = SettingsViewModel.getViewModelFactory(
            application,
            sharingInteractor,
            settingsInteractor
        )

        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnShareApp = findViewById<TextView>(R.id.btn_share_app)
        val btnWriteToSupport = findViewById<TextView>(R.id.btn_write_to_support)
        val btnTermsOfUse = findViewById<TextView>(R.id.btn_terms_of_use)
        themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        btnBack.setOnClickListener {
            finish()
        }

        viewModel.themeSettings.observe(this) { themeSettings ->
            themeSwitcher.isChecked = themeSettings.isDarkThemeEnabled
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        btnShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        btnWriteToSupport.setOnClickListener() {
            viewModel.writeToSupport()
        }

        btnTermsOfUse.setOnClickListener {
            viewModel.termsOfUse()

        }
    }

}
