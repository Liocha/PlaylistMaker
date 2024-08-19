package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.media.ui.adapter.MediaLibraryPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private lateinit var binding: FragmentMediaBinding
    private var tabMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaLibraryPagerAdapter(getChildFragmentManager(), lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.featured_tracks)
                1 -> getString(R.string.playlists)
                else -> getString(R.string.tab_placeholder)
            }
        }

        tabMediator?.attach()
    }

    override fun onDestroy() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroy()
    }

    companion object {
        fun newInstance() = MediaFragment()

    }
}
