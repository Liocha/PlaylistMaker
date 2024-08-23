package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.NavigationState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


open class CreatePlaylistFragment : Fragment() {

    lateinit var binding: FragmentCreatePlaylistBinding
    open val viewModel: CreatePlaylistViewModel by viewModel()
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.editText?.addTextChangedListener { text -> viewModel.onNameChanged(text.toString()) }
        binding.description.editText?.addTextChangedListener { text ->
            viewModel.onDescriptionChanged(
                text.toString()
            )
        }
        binding.btnCreate.setOnClickListener { viewModel.onCreateHandler() }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.cover.setImageURI(uri)
                    binding.cover.setBackgroundResource(0)
                    viewModel.setSelectedImageUri(uri)
                }
            }

        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            viewModel.onCreateHandler()
        }

        viewModel.canCreatePlaylist.observe(viewLifecycleOwner) {
            binding.btnCreate.isEnabled = it
        }

        binding.btnBack.setOnClickListener { viewModel.btnBackHandler() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.btnBackHandler()
        }

        viewModel.navigationState.observe(viewLifecycleOwner) { navigationState ->
            when (navigationState) {
                NavigationState.NavigateBack -> navigateBack()
                NavigationState.ShowDialogBeforeNavigateBack -> showDialog()
                is NavigationState.PlaylistCreated -> playlistCreated(navigationState.playlistName)
                NavigationState.DefaultNothing -> {}
            }
            viewModel.resetNavigationState()
        }
    }

    private fun playlistCreated(playlistName: String) {
        val message = getString(R.string.playlist_created_message, playlistName)
        val customSnackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                resources.getColor(
                    R.color.backgroundTintSnackbar,
                    requireContext().theme
                )
            )
            .setTextColor(
                resources.getColor(
                    R.color.actionTextColorSnackbar,
                    requireContext().theme
                )
            )
        customSnackbar.show()
        navigateBack()
    }

    private fun showDialog() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title_finish_creation))
            .setMessage(getString(R.string.dialog_message_unsaved_data))
            .setNeutralButton(getString(R.string.dialog_button_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_button_finish) { dialog, which ->
                navigateBack()
            }
        confirmDialog.show()
    }

    private fun navigateBack() {
        try {
            findNavController().popBackStack()
        } catch (e: IllegalStateException) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance() = CreatePlaylistFragment()
    }
}