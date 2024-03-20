package com.example.playlistmaker

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.data.api.model.Track
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audioplayer)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra("TRACK", Track::class.java)
        track?.let {

            val artistNameTextView = findViewById<TextView>(R.id.artistName)
            val trackNameTextView =  findViewById<TextView>(R.id.trackName)
            val trackTimeTextView = findViewById<TextView>(R.id.trackTimeValue)
            val collectionNameTextView = findViewById<TextView>(R.id.collectionNameValue)
            val releaseDateTextView = findViewById<TextView>(R.id.releaseDateValue)
            val primaryGenreNameTextView = findViewById<TextView>(R.id.primaryGenreNameValue)
            val countryTextView = findViewById<TextView>(R.id.countryValue)
            val coverImageView = findViewById<ImageView>(R.id.cover)
            val radiusInPx =
                this.resources.getDimension(R.dimen.corner_radius_search_item_img).toInt()

            fun getCoverArtwork() = it.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val releaseDate = LocalDateTime.parse(it.releaseDate, formatter)

            artistNameTextView.text = it.artistName
            trackNameTextView.text = it.trackName
            trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            collectionNameTextView.text = it.collectionName
            releaseDateTextView.text = releaseDate.year.toString()
            primaryGenreNameTextView.text = it.primaryGenreName
            countryTextView.text = it.country
            Glide.with(coverImageView).load(getCoverArtwork()).centerCrop().transform(
                RoundedCorners(radiusInPx)
            )
                .placeholder(R.drawable.placeholder_artwork).into(coverImageView)

        }
    }
}

