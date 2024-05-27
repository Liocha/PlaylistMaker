package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun openLink(termsLink: String)
    fun openEmail(supportEmailData: EmailData)
    fun shareLink(shareAppLink: String)
}