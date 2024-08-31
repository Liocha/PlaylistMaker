package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.view_model.UpdatePlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdatePlaylistFragment : CreatePlaylistFragment() {
    override val viewModel: UpdatePlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getInt("playlistId")
        playlistId?.let {
            viewModel.loadPlaylist(it)
        }

        binding.header.text = getString(R.string.header_edit_playlist)
        binding.btnCreate.text = getString(R.string.save)

        viewModel.playlistData.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                binding.name.editText?.setText(it.name)
                binding.description.editText?.setText(it.description)
                binding.cover.setImageURI(it.pathCover)
            }
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        binding.btnCreate.setOnClickListener {
            viewModel.onSaveHandler()
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

    }
}
