package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ResourceShareProvider
import com.example.playlistmaker.sharing.domain.model.EmailData

class ResourceShareProviderImpl(private val context: Context) : ResourceShareProvider {
    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.default_email),
            subject = context.getString(R.string.default_email_subject),
            text = context.getString(R.string.default_email_text)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer_link)
    }

    override fun getShareAppLink(): String {
        return context.getString(R.string.default_text_in_messenger)
    }

}