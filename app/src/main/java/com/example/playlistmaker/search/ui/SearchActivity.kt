package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.activity.AudioplayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.view_model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var searchItemsView: RecyclerView
    private lateinit var emptySearchPlaceholder: TextView
    private lateinit var connectionErrorPlaceholder: ViewGroup
    private lateinit var btnRefreshSearch: Button
    private lateinit var searchHistoryWidget: ViewGroup
    private lateinit var listHistoryItems: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var searchItemAdapter: SearchItemAdapter
    private lateinit var historyClearBtn: Button

    private lateinit var historyItemAdapter: HistoryItemAdapter

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentConsumerRunnable: Runnable? = null
    private var isClickAllowed = true

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchTracksUseCase = Creator.provideSearchTracksUseCase()
        val getSearchHistoryUseCase =
            Creator.provideGetSearchHistoryUseCase(applicationContext)
        val saveSearchHistoryUseCase =
            Creator.provideSaveSearchHistoryUseCase(applicationContext)
        val clearSearchHistoryUseCase =
            Creator.provideClearSearchHistoryUseCase(applicationContext)

        val viewModelFactory = SearchViewModel.getViewModelFactory(
            application,
            searchTracksUseCase,
            getSearchHistoryUseCase,
            saveSearchHistoryUseCase,
            clearSearchHistoryUseCase
        )

        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[SearchViewModel::class.java]


        val searchInput = findViewById<EditText>(R.id.search_input)
        val searchClearButton = findViewById<ImageView>(R.id.search_clear_btn)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        emptySearchPlaceholder = findViewById(R.id.empty_search_placeholder)
        connectionErrorPlaceholder = findViewById(R.id.connection_error_placeholder)
        btnRefreshSearch = findViewById(R.id.btn_refresh_search)
        progressBar = findViewById(R.id.progress_bar)
        searchHistoryWidget = findViewById(R.id.search_history_widget)
        listHistoryItems = findViewById(R.id.list_of_history_items)
        historyClearBtn = findViewById(R.id.history_clear_btn)
        searchItemsView = findViewById(R.id.list_of_search_items)

        btnBack.setOnClickListener {
            finish()
        }

        searchClearButton.setOnClickListener {
            viewModel.clearSearchInput()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }

        viewModel.searchQuery.observe(this) { query ->
            if (searchInput.text.toString() != query) {
                searchInput.setText(query)
                searchInput.setSelection(query.length)
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                viewModel.onSearchQueryChanged(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        searchInput.addTextChangedListener(simpleTextWatcher)

        historyItemAdapter = HistoryItemAdapter {
            if (clickDebounce()) {
                val displayIntent = Intent(this, AudioplayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, it)
                }
                startActivity(displayIntent)
            }
        }

        searchItemAdapter = SearchItemAdapter {
            if (clickDebounce()) {
                viewModel.addTrackToSearchHistory(it)
                val displayIntent = Intent(this, AudioplayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, it)
                }
                startActivity(displayIntent)
            }
        }

        searchItemsView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchItemAdapter
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
            }
            false
        }

        btnRefreshSearch.setOnClickListener {
            viewModel.repeatLastSearchQuery()
        }

        listHistoryItems.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyItemAdapter
        }

        historyClearBtn.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.onSearchFocusGained()
            }
        }

        viewModel.state.observe(this) {
            render(it)
        }

        viewModel.searchHistory.observe(this) { history ->
            historyItemAdapter.updateData(history)
        }

        viewModel.isSearchHistoryVisible.observe(
            this
        ) { showHistory ->
            if (showHistory) {
                searchHistoryWidget.visibility = View.VISIBLE
                connectionErrorPlaceholder.isVisible = false
                emptySearchPlaceholder.isVisible = false
            } else {
                searchHistoryWidget.visibility = View.GONE
            }
        }

        viewModel.showToast.observe(this) {
            showToast(it)
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            SearchState.Loadind -> showLoading()
        }
    }

    private fun showLoading() {
        hideAllWidgets()
        viewModel.setSearchHistoryVisible(false)
        progressBar.isVisible = true
    }

    private fun showError() {
        hideAllWidgets()
        connectionErrorPlaceholder.isVisible = true
    }

    private fun showEmpty() {
        hideAllWidgets()
        emptySearchPlaceholder.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        hideAllWidgets()
        searchItemAdapter.updateData(tracks)
        searchItemsView.isVisible = true
    }

    private fun hideAllWidgets() {
        emptySearchPlaceholder.isVisible = false
        connectionErrorPlaceholder.isVisible = false
        progressBar.isVisible = false
        searchItemsView.isVisible = false
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_KEY = "TRACK"

    }

    override fun onDestroy() {
        if (currentConsumerRunnable != null) {
            mainHandler.removeCallbacks(currentConsumerRunnable!!)
        }
        super.onDestroy()
    }

    fun showToast(text: String) {
        if (text.isNotEmpty()) {
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }

}