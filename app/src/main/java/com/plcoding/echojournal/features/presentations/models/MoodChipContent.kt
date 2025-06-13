package com.plcoding.echojournal.features.presentations.models

import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.util.UiText

data class MoodChipContent(
    val iconsRes: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)