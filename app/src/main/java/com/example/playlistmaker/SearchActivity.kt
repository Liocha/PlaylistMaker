package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.api.ITunesService
import com.example.playlistmaker.data.api.model.Response
import com.example.playlistmaker.data.api.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesService::class.java)

    private var inputSearchText: String = DEF_TEXT_IN_SEARCH_FIELD
    private lateinit var searchInput: EditText
    private lateinit var searchItemsView: RecyclerView
    private lateinit var emptySearchPlaceholder: TextView
    private lateinit var connectionErrorPlaceholder: ViewGroup
    private lateinit var btnRefreshSearch: Button

    private val tracks = mutableListOf<Track>()
    private val searchItemAdapter = SearchItemAdapter(tracks)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.search_input)
        val searchClearButton = findViewById<ImageView>(R.id.search_clear_btn)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        emptySearchPlaceholder = findViewById(R.id.empty_search_placeholder)
        connectionErrorPlaceholder = findViewById(R.id.connection_error_placeholder)
        btnRefreshSearch = findViewById(R.id.btn_refresh_search)

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

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    tracks.clear()
                    searchItemAdapter.notifyDataSetChanged()
                }
            }

        }

        searchInput.addTextChangedListener(simpleTextWatcher)
        searchItemsView = findViewById(R.id.list_of_search_items)

        searchItemsView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchItemAdapter
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(inputSearchText)
            }
            false
        }

        btnRefreshSearch.setOnClickListener {
            search(inputSearchText)
        }

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

    private fun search(text: String) {
        iTunesService.search(text).enqueue(object : Callback<Response> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                emptySearchPlaceholder.isVisible = false
                connectionErrorPlaceholder.isVisible = false
                if (response.isSuccessful) {
                    tracks.clear()
                    val result: List<Track> = response.body()?.results ?: mutableListOf()
                    tracks.addAll(result)
                    searchItemAdapter.notifyDataSetChanged()
                    if (result.isEmpty()) {
                        emptySearchPlaceholder.isVisible = true
                    }
                } else {
                    tracks.clear()
                    searchItemAdapter.notifyDataSetChanged()
                    connectionErrorPlaceholder.isVisible = true
                    showToast(getString(R.string.server_response_code, response.code().toString()))

                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                showToast(t.message.toString())
                tracks.clear()
                searchItemAdapter.notifyDataSetChanged()
                connectionErrorPlaceholder.isVisible = true
            }

        })
    }

    fun showToast(text: String) {
        if (text.isNotEmpty()) {
            Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                .show()
        }
    }

}