package com.plcoding.echojournal.features.presentations.echos.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.plcoding.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.plcoding.echojournal.features.presentations.models.EchoUi
import com.plcoding.echojournal.features.presentations.models.RelativePosition
import com.plcoding.echojournal.features.presentations.models.TrackSizeInfo
import com.plcoding.echojournal.features.presentations.preview.PreviewModels

private val noVerticaLineAboveIconModifier = Modifier.padding(top = 16.dp)
private val noVerticaLineBelowIconModifier = Modifier.height(8.dp)

@Composable
fun EchoTimelineItem(
    echoUi: EchoUi,
    relativePosition: RelativePosition,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            if(relativePosition != RelativePosition.SINGLE_ENTRY) {
                VerticalDivider(
                    modifier = when(relativePosition) {
                        RelativePosition.FIRST -> noVerticaLineAboveIconModifier
                        RelativePosition.LAST -> noVerticaLineBelowIconModifier
                        RelativePosition.IN_BETWEEN -> Modifier
                        else -> Modifier
                    }
                )
            }

            Image(
                imageVector = ImageVector.vectorResource(echoUi.mood.iconSet.fill),
                contentDescription = echoUi.title,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))

        EchoCard(
            echoUi = echoUi,
            onTrackSizeAvailable = onTrackSizeAvailable,
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun EchoTimelineItemPreview() {
    EchoJournalTheme {
        EchoTimelineItem(
            echoUi = PreviewModels.echoUi,
            relativePosition = RelativePosition.SINGLE_ENTRY,
            onPauseClick = {},
            onPlayClick = {},
            onTrackSizeAvailable = {},
            modifier = Modifier
        )
    }
}