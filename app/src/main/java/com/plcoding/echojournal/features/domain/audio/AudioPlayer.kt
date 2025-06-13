package com.plcoding.echojournal.features.domain.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val activeTrack: StateFlow<AudioTrack?>
    fun play(filePath: String, onComplete: () -> Unit)
    fun stop()
    fun resume()
    fun pause()
}