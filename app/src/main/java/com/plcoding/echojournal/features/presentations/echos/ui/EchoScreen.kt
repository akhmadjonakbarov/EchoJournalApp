package com.plcoding.echojournal.features.presentations.echos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.core.presentation.designsystem.theme.bgGradient
import com.plcoding.echojournal.features.presentations.echos.components.EchoFilterRow
import com.plcoding.echojournal.features.presentations.echos.components.EchoList
import com.plcoding.echojournal.features.presentations.echos.components.EchoRecordingSheet
import com.plcoding.echojournal.features.presentations.echos.components.EchosEmptyBackground
import com.plcoding.echojournal.features.presentations.echos.components.EchosTopBar
import com.plcoding.echojournal.features.presentations.echos.components.RecordFloatingActionButton
import com.plcoding.echojournal.features.presentations.echos.logic.EchosAction
import com.plcoding.echojournal.features.presentations.echos.logic.EchosState
import com.plcoding.echojournal.features.presentations.models.RecordingState

@Composable
fun EchosScreen(
    state: EchosState,
    onAction: (EchosAction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            RecordFloatingActionButton(
                onClick = {
                    onAction(EchosAction.OnFabClick)
                }
            )
        },
        topBar = {
            EchosTopBar(
                onSettingsClick = {
                    onAction(EchosAction.OnSettingsClick)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = MaterialTheme.colorScheme.bgGradient
                )
                .padding(innerPadding)
        ) {
            EchoFilterRow(
                moodChipContent = state.moodChipContent,
                hasActiveMoodFilters = state.hasActiveMoodFilters,
                selectedEchoFilterChip = state.selectedEchoFilterChip,
                moods = state.moods,
                topicChipTitle = state.topicChipTitle,
                hasActiveTopicFilters = state.hasActiveTopicFilters,
                topics = state.topics,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxWidth()
            )
            when {
                state.isLoadingData -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                !state.hasEchosRecorded -> {
                    EchosEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                else -> {
                    EchoList(
                        sections = state.echoDaySections,
                        onPlayClick = {
                            onAction(EchosAction.OnPlayEchoClick(it))
                        },
                        onPauseClick = {
                            onAction(EchosAction.OnPauseRecordingClick)
                        },
                        onTrackSizeAvailable = { trackSize ->
                            onAction(EchosAction.OnTrackSizeAvailable(trackSize))
                        }
                    )
                }
            }
        }

        if (state.recordingState in listOf(RecordingState.NORMAL, RecordingState.PAUSED)) {
            EchoRecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.NORMAL,
                onDismiss = { onAction(EchosAction.OnCancelRecording) },
                onPauseClick = { onAction(EchosAction.OnPauseRecordingClick) },
                onResumeClick = { onAction(EchosAction.OnResumeRecordingClick) },
                onCompleteRecording = { onAction(EchosAction.OnCompleteRecording) },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    EchoJournalTheme {
        EchosScreen(
            state = EchosState(
                isLoadingData = false,
                hasEchosRecorded = false
            ),
            onAction = {}
        )
    }
}