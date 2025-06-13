package com.plcoding.echojournal.features.presentations.models

import com.plcoding.echojournal.features.presentations.util.toReadableTime
import kotlin.time.Duration
import java.time.Instant as JavaInstant

data class EchoUi(
    val id: Int,
    val title: String,
    val mood: MoodUi,
    val recordedAt: JavaInstant,
    val note: String?,
    val topics: List<String>,
    val audioFilePath: String,
    val amplitudes: List<Float>,
    val playbackTotalDuration: Duration,
    val playbackCurrentDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED
) {
    val formattedRecordedAt = recordedAt.toReadableTime()
    val playbackRatio = (playbackCurrentDuration / playbackTotalDuration).toFloat()
}