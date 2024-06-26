package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistCollectionBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistCollectionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCollectionFragment : Fragment() {

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
}