package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    private var inputSearchText: String = DEF_TEXT_IN_SEARCH_FIELD
    private lateinit var searchInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchInput = findViewById(R.id.search_input)
        val searchClearButton = findViewById<ImageView>(R.id.search_clear_btn)
        val btnBack = findViewById<ImageView>(R.id.btn_back)


        btnBack.setOnClickListener {
            finish()
        }

        searchClearButton.setOnClickListener {
            searchInput.setText("")
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

            }

        }

        searchInput.addTextChangedListener(simpleTextWatcher)

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

}