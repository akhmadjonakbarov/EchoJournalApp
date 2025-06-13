package com.plcoding.echojournal.features.presentations.util

import com.plcoding.echojournal.features.domain.data_source.Echo
import com.plcoding.echojournal.features.presentations.models.EchoUi
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import kotlin.time.Duration


fun Echo.toEchoUi(
    currentPlaybackDuration: Duration = Duration.ZERO,
    playbackState: PlaybackState = PlaybackState.STOPPED
): EchoUi {
    return EchoUi(
        id = id!!,
        title = title,
        mood = MoodUi.valueOf(mood.name),
        recordedAt = recordedAt,
        note = note,
        topics = topics,
        amplitudes = audioAmplitudes,
        playbackTotalDuration = audioPlaybackLength,
        audioFilePath = audioFilePath,
        playbackCurrentDuration = currentPlaybackDuration,
        playbackState = playbackState
    )
}