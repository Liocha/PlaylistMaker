package com.example.playlistmaker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce<T>(
    private val delayMillis: Long,
    private val coroutineScope: CoroutineScope,
    private val useLastParam: Boolean,
    private val action: (T) -> Unit
) {
    private var debounceJob:Job? = null
    fun debounce( parapm: T) {
        if (useLastParam) {
            debounceJob?.cancel()
        }

        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(parapm)
            }
        }
    }

    fun cancel() {
        debounceJob?.cancel()
    }
}