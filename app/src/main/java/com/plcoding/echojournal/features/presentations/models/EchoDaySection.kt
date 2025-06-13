package com.plcoding.echojournal.features.presentations.models

import com.plcoding.echojournal.core.util.UiText

data class EchoDaySection(
    val dateHeader: UiText,
    val echos: List<EchoUi>
)