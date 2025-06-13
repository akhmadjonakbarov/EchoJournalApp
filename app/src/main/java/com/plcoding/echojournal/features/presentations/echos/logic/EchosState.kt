package com.plcoding.echojournal.features.presentations.echos.logic

import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.plcoding.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import com.plcoding.echojournal.core.util.UiText
import com.plcoding.echojournal.features.presentations.models.EchoDaySection
import com.plcoding.echojournal.features.presentations.models.EchoUi
import com.plcoding.echojournal.features.presentations.models.MoodChipContent
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.RecordingState
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration

data class EchosState(
    val echos: Map<UiText, List<EchoUi>> = emptyMap(),
    val hasEchosRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val recordingElapsedDuration: Duration = Duration.ZERO,
    val hasActiveMoodFilters: Boolean = false,
    val recordingState: RecordingState = RecordingState.NOT_RECORDING,
    val isLoadingData: Boolean = true,
    val moods: List<Selectable<MoodUi>> = emptyList(),
    val topics: List<Selectable<String>> = listOf("Love", "Happy", "Work").asUnselectedItems(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedEchoFilterChip: EchoFilterChip? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
) {
    val echoDaySections = echos
        .toList()
        .map { (dateHeader, echos) ->
            EchoDaySection(dateHeader, echos)
        }
    val formattedRecordDuration: String
        get() {
            val minutes = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val seconds = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val centiSeconds =
                ((recordingElapsedDuration.inWholeMilliseconds % 1000) / 10.0).roundToInt()
            return String.format(
                locale = Locale.US,
                format = "%02d:%02d:%02d",
                minutes, seconds, centiSeconds
            )
        }
}