package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media.ui.adapter.FavoritesItemAdapter
import com.example.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.view_model.FavoritesState
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private lateinit var favoritesItemAdapter: FavoritesItemAdapter
    private lateinit var favoritesItemsView: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }


    private val viewModel: FavoriteTracksViewModel by viewModel()

    lateinit var binding: FragmentFavoriteTracksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesItemsView = binding.listOfFavoritesItems

        favoritesItemAdapter = FavoritesItemAdapter { track ->
            val action = MediaFragmentDirections.actionMediaFragmentToAudioplayerActivity(track)
            findNavController().navigate(action)
        }


        favoritesItemsView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesItemAdapter
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        emptyStateContainer = binding.emptyStateContainer
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        favoritesItemAdapter.updateData(tracks)
        emptyStateContainer.visibility = View.GONE
        favoritesItemsView.visibility = View.VISIBLE

    }

    private fun showEmpty() {
        favoritesItemsView.visibility = View.GONE
        emptyStateContainer.visibility = View.VISIBLE
    }

}