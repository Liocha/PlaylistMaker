package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.view_model.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
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

    private val viewModel by viewModel<SearchViewModel>()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = binding.searchInput
        val searchClearButton = binding.searchClearBtn

        emptySearchPlaceholder = binding.emptySearchPlaceholder
        connectionErrorPlaceholder = binding.connectionErrorPlaceholder
        btnRefreshSearch = binding.btnRefreshSearch
        progressBar = binding.progressBar
        searchHistoryWidget = binding.searchHistoryWidget
        listHistoryItems = binding.listOfHistoryItems
        historyClearBtn = binding.historyClearBtn
        searchItemsView = binding.listOfSearchItems


        searchClearButton.setOnClickListener {
            viewModel.clearSearchInput()
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
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
                val action = SearchFragmentDirections.actionSearchFragmentToAudioplayerActivity(it)
                findNavController().navigate(action)
            }
        }

        searchItemAdapter = SearchItemAdapter {
            if (clickDebounce()) {
                viewModel.addTrackToSearchHistory(it)
                val action = SearchFragmentDirections.actionSearchFragmentToAudioplayerActivity(it)
                findNavController().navigate(action)
            }
        }

        searchItemsView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchItemAdapter
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
            }
            false
        }

        btnRefreshSearch.setOnClickListener {
            viewModel.repeatLastSearchQuery()
        }

        listHistoryItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
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

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyItemAdapter.updateData(history)
        }

        viewModel.isSearchHistoryVisible.observe(
            viewLifecycleOwner
        ) { showHistory ->
            if (showHistory) {
                searchHistoryWidget.visibility = View.VISIBLE
                connectionErrorPlaceholder.isVisible = false
                emptySearchPlaceholder.isVisible = false
            } else {
                searchHistoryWidget.visibility = View.GONE
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
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

    override fun onDestroy() {
        if (currentConsumerRunnable != null) {
            mainHandler.removeCallbacks(currentConsumerRunnable!!)
        }
        super.onDestroy()
    }

    fun showToast(text: String) {
        if (text.isNotEmpty()) {
            Toast.makeText(requireActivity().applicationContext, text, Toast.LENGTH_LONG).show()
        }
    }


}