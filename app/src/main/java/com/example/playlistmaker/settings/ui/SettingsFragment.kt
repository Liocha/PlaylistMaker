package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.themeSettings.observe(viewLifecycleOwner) { themeSettings: ThemeSettings ->
            binding.themeSwitcher.isChecked = themeSettings.isDarkThemeEnabled
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.btnShareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.btnWriteToSupport.setOnClickListener() {
            viewModel.writeToSupport()
        }

        binding.btnTermsOfUse.setOnClickListener {
            viewModel.termsOfUse()
        }
    }

    companion object {
        fun newInstance() =
            SettingsFragment()
    }
}
