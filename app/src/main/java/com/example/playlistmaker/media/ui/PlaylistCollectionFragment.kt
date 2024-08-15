package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCollectionBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.adapter.PlaylistCollectionItemAdapter
import com.example.playlistmaker.media.ui.view_model.PlaylistCollectionViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCollectionFragment : Fragment() {

    private lateinit var playlistCollectionItemsView: RecyclerView
    private lateinit var playlistCollectionItemAdapter: PlaylistCollectionItemAdapter
    private lateinit var emptyStateContainer: ConstraintLayout

    companion object {
        fun newInstance() = PlaylistCollectionFragment()
    }

    private val viewModel: PlaylistCollectionViewModel by viewModel()

    lateinit var binding: FragmentPlaylistCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistCollectionItemsView = binding.playlistCollectionItems
        playlistCollectionItemAdapter = PlaylistCollectionItemAdapter(::getCountEnding) { }
        emptyStateContainer = binding.emptyStateContainer

        playlistCollectionItemsView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistCollectionItemAdapter
        }

        var btnCreateNewPlaylist = binding.btnCreatePlaylist
        btnCreateNewPlaylist.setOnClickListener {
            val action = MediaFragmentDirections.actionMediaFragmentToCreatePlaylistFragment()
            findNavController().navigate(action)
        }

        viewModel.state.observe(viewLifecycleOwner) { state -> render(state) }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        playlistCollectionItemsView.visibility = View.GONE
        emptyStateContainer.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistCollectionItemAdapter.updateData(playlists)
        emptyStateContainer.visibility = View.GONE
        playlistCollectionItemsView.visibility = View.VISIBLE
    }

    fun getCountEnding(count: Int): String {
        val remainderPerHundred = count % 100
        val remainderByTen = count % 10

        return when {
            remainderPerHundred in 11..19 -> getString(R.string.track_many)
            remainderByTen == 1 -> getString(R.string.track_singular)
            remainderByTen in 2..4 -> getString(R.string.track_few)
            else -> getString(R.string.track_many)
        }
    }
}