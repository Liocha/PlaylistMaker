package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.example.playlistmaker.sharing.domain.repository.ResourceShareProvider
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData


class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resourceShareProvider: ResourceShareProvider
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return resourceShareProvider.getShareAppLink()
    }

    private fun getTermsLink(): String {
        return resourceShareProvider.getTermsLink()
    }

    private fun getSupportEmailData(): EmailData {
        return resourceShareProvider.getSupportEmailData()
    }
}