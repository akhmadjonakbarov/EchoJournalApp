package com.plcoding.echojournal.features.presentations.preview

import com.plcoding.echojournal.features.presentations.models.EchoUi
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data object PreviewModels {

    val echoUi = EchoUi(
        id = 0,
        title = "My audio memo",
        mood = MoodUi.STRESSED,
        recordedAt = Instant.now(),
        note = (1..50).joinToString(" ") { "Hello" },
        topics = listOf("Love", "Work"),
        amplitudes = (1..30).map { Random.nextFloat() },
        playbackTotalDuration = 250.seconds,
        playbackCurrentDuration = 120.seconds,
        playbackState = PlaybackState.PAUSED,
        audioFilePath = ""
    )
}