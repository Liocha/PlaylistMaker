package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<MaterialButton>(R.id.search_btn)
//        val btnClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажали на Поиск!", Toast.LENGTH_SHORT).show()
//            }
//        }
//        btnSearch.setOnClickListener(btnClickListener)
        btnSearch.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }


        val btnMedia = findViewById<MaterialButton>(R.id.media_btn)
        btnMedia.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на Медиатека!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }
        val btnSettings = findViewById<MaterialButton>(R.id.settings_btn)
        btnSettings.setOnClickListener {
            //Toast.makeText(this@MainActivity,"Нажали на Настройки!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

    }
}