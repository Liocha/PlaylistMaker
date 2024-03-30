package com.example.playlistmaker

import android.media.MediaPlayer
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
import com.example.playlistmaker.data.api.model.Track
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_PLAYBACK_COMPLETED = 4
        private const val DEFAULT_TRACK_TIME = "00:00"
    }

    private lateinit var playButton: ImageButton

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var trackDownloadUrl: String

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var currentTrackTime: TextView
    private lateinit var currentPositionSetter: Runnable


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
        preparePlayer()
        playButton.setOnClickListener { playbackControl() }
        currentTrackTime = findViewById(R.id.currentTrackTime)

        currentPositionSetter = object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 1000)
                if (playerState == STATE_PLAYING) {
                    setCurrentPosition()
                }
            }
        }
        setCurrentPosition(DEFAULT_TRACK_TIME)
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PLAYBACK_COMPLETED
            mainHandler.removeCallbacks(currentPositionSetter)
            setCurrentPosition(DEFAULT_TRACK_TIME)
        }
    }

    private fun setCurrentPosition(customValue: String? = null) {
        currentTrackTime.text = customValue
            ?: SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(currentPositionSetter)
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackDownloadUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playButton.setImageResource(R.drawable.pause_button)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playButton.setImageResource(R.drawable.play_button)
        mainHandler.removeCallbacks(currentPositionSetter)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED, STATE_PLAYBACK_COMPLETED -> {
                startPlayer()
                mainHandler.postDelayed(currentPositionSetter, 200)
            }
        }

    }
}

