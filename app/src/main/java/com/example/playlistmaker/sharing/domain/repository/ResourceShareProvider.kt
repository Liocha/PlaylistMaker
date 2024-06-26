package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.sharing.domain.model.EmailData

interface ResourceShareProvider {
    fun getSupportEmailData() : EmailData
    fun getTermsLink() : String
    fun getShareAppLink() : String
}