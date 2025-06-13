package com.plcoding.echojournal.features.presentations.components.play

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.core.util.Pause
import com.plcoding.echojournal.core.util.defaultShadow
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState

@Composable
fun PlaybackButton(
    modifier: Modifier = Modifier,
    playbackState: PlaybackState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    colors: IconButtonColors,
) {
    FilledIconButton(
        onClick = {
            when (playbackState) {
                PlaybackState.PLAYING -> onPauseClick()
                PlaybackState.PAUSED,
                PlaybackState.STOPPED -> onPlayClick()
            }
        },
        colors = colors,
        modifier = modifier.defaultShadow()
    ) {
        Icon(
            imageVector = when (playbackState) {
                PlaybackState.PLAYING -> Icons.Filled.Pause
                PlaybackState.PAUSED, PlaybackState.STOPPED -> Icons.Filled.PlayArrow
            }, contentDescription = ""
        )
    }

}

@Preview
@Composable
private fun PlaybackButtonPreview() {
    EchoJournalTheme {
        PlaybackButton(
            playbackState = PlaybackState.PLAYING,
            onPauseClick = {},
            onPlayClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MoodUi.SAD.colorSet.vivid
            )
        )
    }
}