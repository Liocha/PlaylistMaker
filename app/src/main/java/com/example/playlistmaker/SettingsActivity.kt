package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnShareApp = findViewById<TextView>(R.id.btn_share_app)
        val btnWriteToSupport = findViewById<TextView>(R.id.btn_write_to_support)
        val btnTermsOfUse = findViewById<TextView>(R.id.btn_terms_of_use)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        btnBack.setOnClickListener {
            finish()
        }

        themeSwitcher.setChecked((applicationContext as App).darkTheme)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit().putBoolean(DARK_THEME_ENABLED_KEY, checked).apply()
        }

        btnShareApp.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.default_text_in_messenger))
                type = "text/plain"
            }
            val shareAppIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareAppIntent)
        }


        btnWriteToSupport.setOnClickListener() {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.default_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.default_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.default_email_text))
                startActivity(Intent.createChooser(this, null))
            }
        }


        btnTermsOfUse.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.practicum_offer_link))
            }
            val termsOfUseIntent = Intent.createChooser(sendIntent, null)
            startActivity(termsOfUseIntent)
        }
    }
}
