package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val btnShareApp = findViewById<TextView>(R.id.btn_share_app)
        btnShareApp.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.default_text_in_messenger))
                type = "text/plain"
            }
            val shareAppIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareAppIntent)
        }

        val btnWriteToSupport = findViewById<TextView>(R.id.btn_write_to_support)
        btnWriteToSupport.setOnClickListener() {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.default_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.default_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.default_email_text))
            }
            val writeToSupportIntent = Intent.createChooser(sendIntent, null)
            startActivity(writeToSupportIntent)
        }

        val btnTermsOfUse = findViewById<TextView>(R.id.btn_terms_of_use)
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
