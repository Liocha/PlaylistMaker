package com.example.playlistmaker.utils

import android.content.Context
import com.example.playlistmaker.R

object TextHelper {
    fun getCountEnding(context: Context, count: Int): String {
        val remainderPerHundred = count % 100
        val remainderByTen = count % 10

        return when {
            remainderPerHundred in 11..19 -> context.getString(R.string.track_many)
            remainderByTen == 1 -> context.getString(R.string.track_singular)
            remainderByTen in 2..4 -> context.getString(R.string.track_few)
            else -> context.getString(R.string.track_many)
        }
    }
}