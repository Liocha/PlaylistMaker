package com.example.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.CreatePlaylistFragment
import com.example.playlistmaker.player.ui.adapter.PlaylistItemAdapter
import com.example.playlistmaker.player.ui.view_model.AudioPlayerScreenState
import com.example.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.TextHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var favoritesButton: ImageButton
    private lateinit var btnAddToPlaylist: ImageButton
    private lateinit var playlistItemAdapter: PlaylistItemAdapter
    private lateinit var playlistsItemsView: RecyclerView

    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var overlay: View

    private lateinit var main: ScrollView

    private val track: Track? by lazy {
        intent?.getParcelableExtra("track")
    }

    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track) }

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

        if (track == null) {
            finish()
            return
        }

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        playButton = findViewById(R.id.btnPlay)
        favoritesButton = findViewById(R.id.btnLike)

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

        viewModel.playButtonState.observe(this) { iconResource ->
            playButton.setImageResource(iconResource)
        }

        playButton.setOnClickListener { viewModel.playbackControl() }
        favoritesButton.setOnClickListener { viewModel.onFavoriteClicked() }

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                favoritesButton.setImageResource(R.drawable.ic_favorite_track_active)
                favoritesButton.setSelected(isFavorite)
            } else {
                favoritesButton.setImageResource(R.drawable.ic_favorite_track_inactive)
                favoritesButton.setSelected(isFavorite)
            }
        }

        playlistsItemsView = findViewById(R.id.list_of_playlist)
        playlistItemAdapter =
            PlaylistItemAdapter({ count ->
                TextHelper.getCountEnding(
                    this,
                    count
                )
            }) { viewModel.addTrackToPlaylist(it) }

        playlistsItemsView.apply {
            layoutManager = LinearLayoutManager(this@AudioplayerActivity)
            adapter = playlistItemAdapter
        }

        viewModel.playlists.observe(this) { playlists ->
            playlistItemAdapter.updateData(playlists)
        }

        btnAddToPlaylist = findViewById(R.id.btnAdd)

        overlay = findViewById<View>(R.id.overlay)

        bottomSheetContainer = findViewById(R.id.standard_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        btnAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        viewModel.toastMessage.observe(this) { message ->
            val text = getString(message.first, message.second)
            Snackbar.make(findViewById(R.id.layout_wrap), text, Snackbar.LENGTH_SHORT).show()
        }

        val btnAddToPlaylist = findViewById<TextView>(R.id.btn_create_playlist)
        btnAddToPlaylist.setOnClickListener {
            showCreatePlaylistFragment()
        }

        fragmentContainer = findViewById(R.id.fragment_container)
        main = findViewById(R.id.main)

        viewModel.trackAdded.observe(this) { isAdded ->
            if (isAdded) {
               bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun showCreatePlaylistFragment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val fragment = CreatePlaylistFragment.newInstance()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                main.visibility = View.VISIBLE
                fragmentContainer.visibility = View.GONE
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        overlay.visibility = View.GONE
        main.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
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
        val releaseDate =
            track.releaseDate?.let { LocalDateTime.parse(it, formatter).year.toString() } ?: ""
        val collectionNameGroup = findViewById<Group>(R.id.collectionNameGroup)
        if (track.collectionName.isEmpty()) {
            collectionNameGroup.visibility = View.GONE
        }

        artistNameTextView.text = track.artistName
        trackNameTextView.text = track.trackName
        trackTimeTextView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionNameTextView.text = track.collectionName
        releaseDateTextView.text = releaseDate
        primaryGenreNameTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        Glide.with(coverImageView).load(getCoverArtwork()).centerCrop().transform(
            RoundedCorners(radiusInPx)
        )
            .placeholder(R.drawable.placeholder_artwork).into(coverImageView)

    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            viewModel.handleActivityPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            viewModel.onCleared()
        }
    }
}

