package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.adapter.EditPlaylistItemAdapter
import com.example.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.TextHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : Fragment() {
    lateinit var binding: FragmentEditPlaylistBinding
    val viewModel: EditPlaylistViewModel by viewModel()
    private lateinit var trackItemsView: RecyclerView
    private lateinit var editPlaylistItemAdapter: EditPlaylistItemAdapter
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    lateinit var tracksListBottomSheetContainer: LinearLayout
    lateinit var tracksListBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    lateinit var menuBottomSheetContainer: ConstraintLayout
    lateinit var menuBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getInt("playlistId")
        playlistId?.let {
            viewModel.loadPlaylist(it)
        }

        viewModel.currentPlaylist.observe(viewLifecycleOwner) { playlist ->
            render(playlist)
        }

        viewModel.tracksTime.observe(viewLifecycleOwner) { tracksTime ->
            val timeEnding = TextHelper.getMinuteEnding(requireContext(), tracksTime)
            binding.tracksTime.text = "${tracksTime} ${timeEnding}"
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        trackItemsView = binding.listOfTracksInPlaylist

        editPlaylistItemAdapter = EditPlaylistItemAdapter({ track ->
            val action =
                EditPlaylistFragmentDirections.actionEditPlaylistFragmentToAudioplayerActivity(track)
            findNavController().navigate(action)
        }) { track -> showDialogDeleteTrack(track) }


        trackItemsView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = editPlaylistItemAdapter
        }

        viewModel.playlistTracks.observe(viewLifecycleOwner) {
            editPlaylistItemAdapter.updateData(it)
        }

        binding.btnShare.setOnClickListener { viewModel.sharePlaylist() }

        viewModel.showEmptyPlaylistMessage.observe(viewLifecycleOwner) { status ->
            if (status) {
                val text = getString(R.string.dialog_message_empty_playlist)
                Snackbar.make(binding.wrap, text, Snackbar.LENGTH_SHORT).show()
                viewModel.showNoTracksMessage(false)
            }
        }

        tracksListBottomSheetContainer = binding.trackListBottomSheet
        tracksListBottomSheetBehavior = BottomSheetBehavior.from(tracksListBottomSheetContainer)
        tracksListBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        menuBottomSheetContainer = binding.menuBottomSheet
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetContainer)
            .apply { state = BottomSheetBehavior.STATE_HIDDEN }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.btnEdit.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


        binding.menuBottomSheetSharePlaylist.setOnClickListener { viewModel.sharePlaylist() }
        binding.menuBottomSheetEditPlaylist.setOnClickListener {
            viewModel.currentPlaylist.value?.id?.let {
                val bundle = Bundle().apply {
                    putInt("playlistId", it)
                }
                findNavController().navigate(
                    R.id.action_editPlaylistFragment_to_updatePlaylistFragment,
                    bundle
                )
            }
        }
        binding.menuBottomSheetDeletePlaylist.setOnClickListener { showDialogDeletePlaylist() }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.hideBottomSheetMenu.observe(viewLifecycleOwner) {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun render(playlist: Playlist?) {
        if (playlist != null) {
            if (playlist.pathCover == null) {
                binding.menuBottomSheetPlaylistCover.setImageResource(R.drawable.placeholder_artwork)
                binding.imageCover.setImageResource(R.drawable.placeholder_artwork)
            } else {
                binding.menuBottomSheetPlaylistCover.setImageURI(playlist.pathCover)
                binding.imageCover.setImageURI(playlist.pathCover)
            }

            binding.playlistName.text = playlist.name
            binding.menuBottomSheetPlaylistName.text = playlist.name
            binding.playlistDescription.text = playlist.description

            val counterEnding = TextHelper.getCountEnding(
                requireContext(),
                playlist.tracksCount
            )
            binding.tracksCount.text = "${playlist.tracksCount} ${counterEnding}"
            binding.menuBottomSheetTracksCount.text = "${playlist.tracksCount} ${counterEnding}"

        }
    }

    private fun showDialogDeleteTrack(track: Track) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.want_to_delete_track))
            .setNegativeButton(R.string.no_action) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes_action)) { dialog, which ->
                viewModel.removeTrackFromPlaylist(track)
            }
        confirmDialog.show()
    }

    private fun showDialogDeletePlaylist() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.want_to_delete_playlist))
            .setNegativeButton(R.string.no_action) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes_action)) { dialog, which ->
                viewModel.deletePlaylist()
            }
        confirmDialog.show()
    }
}
