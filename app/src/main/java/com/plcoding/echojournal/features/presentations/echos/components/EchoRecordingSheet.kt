package com.plcoding.echojournal.features.presentations.echos.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.plcoding.echojournal.R

import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.core.presentation.designsystem.theme.buttonGradient
import com.plcoding.echojournal.core.presentation.designsystem.theme.primary90
import com.plcoding.echojournal.core.presentation.designsystem.theme.primary95
import com.plcoding.echojournal.core.util.Mic
import com.plcoding.echojournal.core.util.Pause

private const val PRIMARY_BUTTON_BUBBLE_SIZE_DP = 120
private const val SECONDARY_BUTTON_SIZE_DP = 48


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoRecordingSheet(
    formattedRecordDuration: String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCompleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        SheetContent(
            formattedRecordDuration = formattedRecordDuration,
            isRecording = isRecording,
            onDismiss = onDismiss,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onCompleteRecording = onCompleteRecording,
        )
    }
}

@Composable
fun SheetContent(
    formattedRecordDuration: String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCompleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBubbleSize = PRIMARY_BUTTON_BUBBLE_SIZE_DP.dp
    val secondaryButtonSize = SECONDARY_BUTTON_SIZE_DP.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isRecording) {
                    stringResource(R.string.recording_your_memories)
                } else {
                    stringResource(R.string.recording_paused)
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = formattedRecordDuration,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFeatureSettings = "tnum",
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledRecordingButton(
                onClick = onDismiss, contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ) {
                Icon(
                    imageVector = Icons.Default.Close, contentDescription = "Close"
                )
            }

            val interactionSource = remember {
                MutableInteractionSource()
            }

            val isPressed by interactionSource.collectIsPressedAsState()

            Box(
                modifier = Modifier
                    .size(primaryBubbleSize)
                    .background(
                        color = if (isRecording) {
                            MaterialTheme.colorScheme.primary95
                        } else {
                            Color.Transparent
                        }, shape = CircleShape
                    )
                    .padding(10.dp)
                    .background(
                        color = if (isRecording) {
                            MaterialTheme.colorScheme.primary90
                        } else {
                            Color.Transparent
                        }, shape = CircleShape
                    )
                    .padding(14.dp)
                    .background(
                        brush = MaterialTheme.colorScheme.buttonGradient, shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = if (isRecording) {
                            onCompleteRecording
                        } else {
                            onResumeClick
                        }

                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Check else Icons.Default.Mic,
                    contentDescription = "",
                    tint = Color.White
                )
            }



            FilledRecordingButton(
                onClick = onPauseClick, contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Check,
                    contentDescription = "Pause"
                )
            }
        }
    }
}


@Composable
fun FilledRecordingButton(
    onClick: () -> Unit,
    contentColor: Color,
    containerColor: Color,
    content: @Composable () -> Unit
) {

    val transparentColor = Color.Transparent

    FilledIconButton(
        modifier = Modifier.size(48.dp),
        onClick = onClick, colors = IconButtonColors(
            contentColor = contentColor,
            containerColor = containerColor,
            disabledContentColor = transparentColor,
            disabledContainerColor = transparentColor
        )
    ) {
        content()
    }

}


@Preview
@Composable
private fun SheetContentPreview() {
    EchoJournalTheme {
        SheetContent(
            formattedRecordDuration = "00:10:34",
            isRecording = false,
            onDismiss = {},
            onPauseClick = {},
            onResumeClick = {},
            onCompleteRecording = {}
        )
    }
}