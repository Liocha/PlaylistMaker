package com.example.playlistmaker.presentation

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.consumer.Consumer
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.use_case.implementations.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.implementations.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.implementations.SaveSearchHistoryUseCase

class SearchActivity : AppCompatActivity() {
    private val searhTracksUseCase = Creator.provideSearchTracksUseCase()
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var saveSearchHistoryUseCase: SaveSearchHistoryUseCase
    private lateinit var clearSearchHistoryUseCase: ClearSearchHistoryUseCase

    private var inputSearchText: String = DEF_TEXT_IN_SEARCH_FIELD
    private lateinit var searchInput: EditText
    private lateinit var searchItemsView: RecyclerView
    private lateinit var emptySearchPlaceholder: TextView
    private lateinit var connectionErrorPlaceholder: ViewGroup
    private lateinit var btnRefreshSearch: Button
    private lateinit var searchHistoryWidget: ViewGroup
    private lateinit var listHistoryItems: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val tracks = mutableListOf<Track>()
    private lateinit var searchItemAdapter: SearchItemAdapter
    private lateinit var historyClearBtn: Button

    private val history = mutableListOf<Track>()
    private lateinit var historyItemAdapter: HistoryItemAdapter

    private val mainHandler = Handler(Looper.getMainLooper())
    private var currentConsumerRunnable: Runnable? = null
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        getSearchHistoryUseCase =
            Creator.provideGetSearchHistoryUseCase(this) as GetSearchHistoryUseCase
        saveSearchHistoryUseCase =
            Creator.provideSaveSearchHistoryUseCase(this) as SaveSearchHistoryUseCase
        clearSearchHistoryUseCase =
            Creator.provideClearSearchHistoryUseCase(this) as ClearSearchHistoryUseCase

        searchInput = findViewById(R.id.search_input)
        val searchClearButton = findViewById<ImageView>(R.id.search_clear_btn)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        emptySearchPlaceholder = findViewById(R.id.empty_search_placeholder)
        connectionErrorPlaceholder = findViewById(R.id.connection_error_placeholder)
        btnRefreshSearch = findViewById(R.id.btn_refresh_search)
        progressBar = findViewById(R.id.progress_bar)

        history.addAll(getSearchHistoryUseCase.execute())


        btnBack.setOnClickListener {
            finish()
        }

        searchClearButton.setOnClickListener {
            searchInput.setText("")
            clearSearch()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                inputSearchText = s.toString()
                debounceSearchQuery()

                if (searchInput.hasFocus() && s?.isEmpty() == true) {
                    showHistoryWidget()
                } else {
                    hideHistoryWidget()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    clearSearch()
                }
            }
        }

        searchInput.addTextChangedListener(simpleTextWatcher)
        searchItemsView = findViewById(R.id.list_of_search_items)

        historyItemAdapter = HistoryItemAdapter(history) {
            if (clickDebounce()) {
                val displayIntent = Intent(this, AudioplayerActivity::class.java).apply {
                    putExtra("TRACK", history[it])
                }
                startActivity(displayIntent)
            }
        }

        searchItemAdapter = SearchItemAdapter(tracks) {
            if (clickDebounce()) {
                saveSearchHistoryUseCase.execute(tracks[it])
                val displayIntent =
                    Intent(this, AudioplayerActivity::class.java).apply {
                        putExtra("TRACK", tracks[it])
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
                search()
            }
            false
        }

        btnRefreshSearch.setOnClickListener {
            search()
        }

        searchHistoryWidget = findViewById(R.id.search_history_widget)
        listHistoryItems = findViewById(R.id.list_of_history_items)
        historyClearBtn = findViewById(R.id.history_clear_btn)

        listHistoryItems.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyItemAdapter
        }

        historyClearBtn.setOnClickListener {
            clearSearchHistoryUseCase.execute()
            hideHistoryWidget()
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                showHistoryWidget()
            } else {
                hideHistoryWidget()
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private val searchRunnable = Runnable { search() }

    private fun debounceSearchQuery() {
        mainHandler.removeCallbacks(searchRunnable)
        mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showHistoryWidget() {
        if (getSearchHistoryUseCase.execute().isNotEmpty()) {
            history.apply {
                clear()
                addAll(getSearchHistoryUseCase.execute())
            }
            historyItemAdapter.notifyDataSetChanged()
            searchHistoryWidget.visibility = View.VISIBLE
            connectionErrorPlaceholder.isVisible = false
            emptySearchPlaceholder.isVisible = false
        }
    }

    private fun hideHistoryWidget() {
        searchHistoryWidget.visibility = View.GONE
    }


    private fun clearSearch() {
        emptySearchPlaceholder.isVisible = false
        connectionErrorPlaceholder.isVisible = false
        tracks.clear()
        searchItemAdapter.notifyDataSetChanged()

    }

    companion object {
        private const val CURRENT_TEXT_IN_SEARCH_FIELD = "CURRENT_TEXT_IN_SEARCH_FIELD"
        private const val DEF_TEXT_IN_SEARCH_FIELD = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_TEXT_IN_SEARCH_FIELD, inputSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        this.inputSearchText =
            savedInstanceState.getString(CURRENT_TEXT_IN_SEARCH_FIELD, DEF_TEXT_IN_SEARCH_FIELD)
        searchInput.setText(inputSearchText)
        searchInput.setSelection(inputSearchText.length)
    }

    private fun search() {
        if (inputSearchText.isEmpty()) {
            return
        }
        emptySearchPlaceholder.isVisible = false
        connectionErrorPlaceholder.isVisible = false
        progressBar.isVisible = true
        searhTracksUseCase.execute(inputSearchText, object : Consumer {
            override fun consume(data: ConsumerData) {
                if (currentConsumerRunnable != null) {
                    mainHandler.removeCallbacks(currentConsumerRunnable!!)
                }
                val consumerRunnable = Runnable {
                    progressBar.isVisible = false
                    when (data) {
                        is ConsumerData.Data -> {
                            tracks.clear()
                            tracks.addAll(data.data)
                            searchItemAdapter.notifyDataSetChanged()
                            if (tracks.isEmpty()) {
                                emptySearchPlaceholder.isVisible = true
                            }
                        }

                        is ConsumerData.Error -> {
                            tracks.clear()
                            searchItemAdapter.notifyDataSetChanged()
                            connectionErrorPlaceholder.isVisible = true
                            showToast(data.message)
                        }

                        is ConsumerData.NetworkError -> {
                            showToast(data.message)
                            tracks.clear()
                            searchItemAdapter.notifyDataSetChanged()
                            connectionErrorPlaceholder.isVisible = true
                        }
                    }
                }

                currentConsumerRunnable = consumerRunnable
                mainHandler.post(currentConsumerRunnable!!)
            }

        })
    }

    override fun onDestroy() {
        if (currentConsumerRunnable != null) {
            mainHandler.removeCallbacks(currentConsumerRunnable!!)
        }
        super.onDestroy()
    }

    fun showToast(text: String) {
        if (text.isNotEmpty()) {
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                .show()
        }
    }

}