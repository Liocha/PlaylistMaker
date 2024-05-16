package com.example.playlistmaker.presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlayerListener
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.interfaces.MediaPlayerInteractor
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private var playerState: PlayerState = PlayerState.DEFAULT
    private lateinit var trackDownloadUrl: String
    private lateinit var mediaPlayerInteractor: MediaPlayerInteractor

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var currentTrackTime: TextView
    private lateinit var currentPositionSetter: Runnable
    private val DEFAULT_TRACK_TIME = "00:00"


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

        val track = intent.getParcelableExtra("TRACK", Track::class.java)
        track?.let {

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

            fun getCoverArtwork() = it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val releaseDate = LocalDateTime.parse(it.releaseDate, formatter)

            val collectionNameGroup = findViewById<Group>(R.id.collectionNameGroup)
            if (it.collectionName.isEmpty()) {
                collectionNameGroup.visibility = View.GONE
            }

            artistNameTextView.text = it.artistName
            trackNameTextView.text = it.trackName
            trackTimeTextView.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
            collectionNameTextView.text = it.collectionName
            releaseDateTextView.text = releaseDate.year.toString()
            primaryGenreNameTextView.text = it.primaryGenreName
            countryTextView.text = it.country
            Glide.with(coverImageView).load(getCoverArtwork()).centerCrop().transform(
                RoundedCorners(radiusInPx)
            )
                .placeholder(R.drawable.placeholder_artwork).into(coverImageView)

        }
        playButton = findViewById(R.id.btnPlay)
        trackDownloadUrl = track?.previewUrl.toString()

        mediaPlayerInteractor = Creator.provideMediaPLayerInteractor()
        setupMediaPlayback()

        playButton.setOnClickListener { playbackControl() }
        currentTrackTime = findViewById(R.id.currentTrackTime)

        currentPositionSetter = object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 200)
                if (playerState == PlayerState.PLAYING) {
                    setCurrentPosition()
                }
            }
        }
    }

    private fun setCurrentPosition(customValue: String? = null) {
        currentTrackTime.text = customValue
            ?: SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayerInteractor.getCurrentPosition())
    }

    override fun onPause() {
        playbackControl()
        super.onPause()
        mainHandler.removeCallbacks(currentPositionSetter)
    }

    override fun onDestroy() {
        mediaPlayerInteractor.release()
        super.onDestroy()
        mainHandler.removeCallbacks(currentPositionSetter)
    }


    private fun setupMediaPlayback() {
        mediaPlayerInteractor.preparePlayer(trackDownloadUrl)
        mediaPlayerInteractor.setListener(object : PlayerListener {
            override fun onStateChange(state: PlayerState) {
                playerState = state
                if (state == PlayerState.PLAYBACK_COMPLETED) {
                    playButton.setImageResource(R.drawable.play_button)
                    mainHandler.removeCallbacks(currentPositionSetter)
                    setCurrentPosition(DEFAULT_TRACK_TIME)
                }
            }
        })

    }

    private fun playbackControl() {
        handlerPlaybackState()
        updatePlaybackUi()
    }

    private fun updatePlaybackUi() {
        val iconResource = when (playerState) {
            PlayerState.PLAYING -> R.drawable.pause_button
            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.PLAYBACK_COMPLETED -> R.drawable.play_button
            PlayerState.DEFAULT -> return
        }
        playButton.setImageResource(iconResource)
    }

    private fun handlerPlaybackState() {
        when (playerState) {
            PlayerState.PLAYING -> {
                mediaPlayerInteractor.pausePlayer()
                mainHandler.removeCallbacks(currentPositionSetter)
            }

            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.PLAYBACK_COMPLETED -> {
                mediaPlayerInteractor.startPlayer()
                mainHandler.postDelayed(currentPositionSetter, 200)
            }

            PlayerState.DEFAULT -> {

            }
        }
    }
}

