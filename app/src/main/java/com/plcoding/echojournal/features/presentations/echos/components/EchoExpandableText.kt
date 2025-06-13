package com.plcoding.echojournal.features.presentations.echos.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.echojournal.R

@Composable
fun EchoExpandableText(
    modifier: Modifier = Modifier, text: String, collapseMaxLine: Int = 3,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var isClickable by remember {
        mutableStateOf(false)
    }
    val primary = MaterialTheme.colorScheme.primary
    val showMoreText = stringResource(R.string.show_more)
    var lastCharacterIndex by remember { mutableIntStateOf(0) }
    val textToShow = remember(text, isClickable, isExpanded) {
        buildAnnotatedString {
            when {
                isClickable && !isExpanded -> {
                    val adjustText = text.substring(
                        startIndex = 0, endIndex = lastCharacterIndex
                    ).dropLast(showMoreText.length)
                        .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                    append(adjustText)
                    append("...")
                    withStyle(
                        style = SpanStyle(
                            color = primary,
                            fontWeight = FontWeight.Bold,
                        )
                    ) {

                        append(showMoreText)

                    }
                }

                else -> {
                    append(text)
                }
            }
        }
    }
    Text(
        text = textToShow, maxLines = if (isExpanded) Int.MAX_VALUE else collapseMaxLine,
        modifier = modifier
            .fillMaxWidth()

            .clickable(
                enabled = isClickable
            ) {

                isExpanded = !isExpanded
            }
            .animateContentSize(),
        onTextLayout = {
            if (!isExpanded && it.hasVisualOverflow) {
                isClickable = true
                lastCharacterIndex = it.getLineEnd(lineIndex = collapseMaxLine - 1)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EchoExpandableTextPreview() {
    EchoExpandableText(
        text = buildString {
            repeat(50) {
                append("Hello ")
            }
        },
    )
}