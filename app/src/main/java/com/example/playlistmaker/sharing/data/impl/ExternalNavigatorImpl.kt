package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(val context: Context) : ExternalNavigator {
    override fun openLink(termsLink: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(termsLink)
        }
        val termsOfUseIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(termsOfUseIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
        }
        val openEmailIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(openEmailIntent)
    }

    override fun shareLink(shareAppLink: String) {
        Log.d("ExternalNavigator", "Sharing link with context: $context")
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
            type = "text/plain"
        }
        val shareAppIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareAppIntent)
    }
}