package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private val viewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnShareApp = findViewById<TextView>(R.id.btn_share_app)
        val btnWriteToSupport = findViewById<TextView>(R.id.btn_write_to_support)
        val btnTermsOfUse = findViewById<TextView>(R.id.btn_terms_of_use)
        themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        btnBack.setOnClickListener {
            finish()
        }

        viewModel.themeSettings.observe(this) { themeSettings: ThemeSettings ->
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
