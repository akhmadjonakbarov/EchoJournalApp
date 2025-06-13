package com.plcoding.echojournal.core.util

import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.formatMMSS(): String {
    val totalSecond = this.toLong(DurationUnit.SECONDS)
    val minutes = totalSecond / 60
    val seconds = totalSecond % 60
    return String.format(
        locale = Locale.getDefault(),
        format = "%02d:%02d",
        minutes, seconds
    )
}