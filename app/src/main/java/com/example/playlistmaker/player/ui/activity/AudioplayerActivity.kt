package com.example.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.use_case.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.view_model.AudioPlayerScreenState
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor
    private lateinit var currentTrackTime: TextView
    lateinit var viewModel: AudioPlayerViewModel


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_audioplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra("TRACK", Track::class.java)!!
        mediaPlayerInteractor = Creator.provideMediaPLayerInteractor()
        val viewModelFactory =
            AudioPlayerViewModel.getViewModelFactory(track, mediaPlayerInteractor)
        viewModel = ViewModelProvider(this, viewModelFactory!!)[AudioPlayerViewModel::class.java]

        playButton = findViewById(R.id.btnPlay)

        currentTrackTime = findViewById(R.id.currentTrackTime)

        viewModel.screenState.observe(this) { screenState ->
            when (screenState) {
                is AudioPlayerScreenState.Loading -> {

                }

                is AudioPlayerScreenState.Success -> {
                    updateUi(screenState.track)
                }

                is AudioPlayerScreenState.Error -> {

                }
            }
        }

        viewModel.currentTrackTime.observe(this) { time ->
            currentTrackTime.text = time
        }

        viewModel.playButtonState.observe(this) {iconResource ->
            playButton.setImageResource(iconResource)
        }

        playButton.setOnClickListener { viewModel.playbackControl() }


    }


    private fun updateUi(track: Track) {
        val artistNameTextView = findViewById<TextView>(R.id.artistName)
        val trackNameTextView = findViewById<TextView>(R.id.trackName)
        val trackTimeTextView = findViewById<TextView>(R.id.trackTimeValue)
        val collectionNameTextView = findViewById<TextView>(R.id.collectionNameValue)
        val releaseDateTextView = findViewById<TextView>(R.id.releaseDateValue)
        val primaryGenreNameTextView = findViewById<TextView>(R.id.primaryGenreNameValue)
        val countryTextView = findViewById<TextView>(R.id.countryValue)
        val coverImageView = findViewById<ImageView>(R.id.cover)
        val radiusInPx =
            this.resources.getDimension(R.dimen.corner_radius_search_cover_img).toInt()

        fun getCoverArtwork() = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val releaseDate = LocalDateTime.parse(track.releaseDate, formatter)
        val collectionNameGroup = findViewById<Group>(R.id.collectionNameGroup)
        if (track.collectionName.isEmpty()) {
            collectionNameGroup.visibility = View.GONE
        }

        artistNameTextView.text = track.artistName
        trackNameTextView.text = track.trackName
        trackTimeTextView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionNameTextView.text = track.collectionName
        releaseDateTextView.text = releaseDate.year.toString()
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        Glide.with(coverImageView).load(getCoverArtwork()).centerCrop().transform(
            RoundedCorners(radiusInPx)
        )
            .placeholder(R.drawable.placeholder_artwork).into(coverImageView)

    }

    override fun onPause() {
        super.onPause()
        viewModel.playbackControl()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            viewModel.onCleared()
        }
    }

}

