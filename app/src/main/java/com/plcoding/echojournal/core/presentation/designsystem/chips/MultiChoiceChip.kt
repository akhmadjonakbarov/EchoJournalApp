package com.plcoding.echojournal.core.presentation.designsystem.chips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun MultiChoiceChip(
    displayText: String,
    onClick: () -> Unit, onClearButtonClick: () -> Unit, isClearVisible: Boolean,
    isHighlighted: Boolean, isDropDownVisible: Boolean, dropDownMenu: @Composable () -> Unit,
    modifier: Modifier = Modifier, leadingContent: (@Composable () -> Unit)? = null
) {
    val containerColor = if (isHighlighted) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Transparent
    }

    val borderColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .then(
                if (isHighlighted) {
                    Modifier.shadow(elevation = 4.dp, shape = CircleShape)
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .border(width = 0.5.dp, color = borderColor, shape = CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(
                6.dp
            )
        ) {
            leadingContent?.invoke()
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            AnimatedVisibility(visible = isClearVisible) {
                IconButton(onClick = onClearButtonClick, modifier = Modifier.size(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }
        if (isDropDownVisible) {
            dropDownMenu()
        }
    }


}

@Preview
@Composable
private fun MultiChoiceChipPreview() {
    MultiChoiceChip(
        displayText = "All Topics",
        onClick = {},
        onClearButtonClick = {},
        isClearVisible = false,
        isHighlighted = false,
        isDropDownVisible = true,
        dropDownMenu = {},
        leadingContent = {
            Icon(imageVector = Icons.Default.Build, contentDescription = null)
        }

    )
}