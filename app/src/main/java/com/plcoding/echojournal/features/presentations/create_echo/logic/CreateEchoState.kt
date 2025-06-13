package com.plcoding.echojournal.features.presentations.create_echo.logic

import com.plcoding.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import kotlin.time.Duration

data class CreateEchoState(
    val titleText: String = "",
    val addTopicText: String = "",
    val noteText: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val mood: MoodUi? = null,
    val searchResults: List<Selectable<String>> = emptyList(),
    val showCreateTopicOption: Boolean = false,
    val canSaveEcho: Boolean = false,
    val playbackAmplitudes: List<Float> = emptyList<Float>(),
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val durationPlayed: Duration = Duration.ZERO,
    val topics: List<String> = emptyList<String>(),
    val showConfirmLeaveDialog: Boolean = false
) {
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}