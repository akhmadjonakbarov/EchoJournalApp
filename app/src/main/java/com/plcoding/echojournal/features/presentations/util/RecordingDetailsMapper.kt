package com.plcoding.echojournal.features.presentations.util



import com.plcoding.echojournal.app.navigation.NavigationRoutes
import com.plcoding.echojournal.features.domain.recording.RecordingDetails
import kotlin.time.Duration.Companion.milliseconds

fun RecordingDetails.toCreateEchoRoute(): NavigationRoutes.CreateEcho {
    return NavigationRoutes.CreateEcho(
        recordingPath = this.filePath ?: throw IllegalArgumentException(
            "Recording path can't be null."
        ),
        duration = this.duration.inWholeMilliseconds,
        amplitudes = this.amplitudes.joinToString(";")
    )
}

fun NavigationRoutes.CreateEcho.toRecordingDetails(): RecordingDetails {
    return RecordingDetails(
        duration = this.duration.milliseconds,
        amplitudes = this.amplitudes.split(";").map { it.toFloat() },
        filePath = recordingPath
    )
}