package com.plcoding.echojournal.core.util

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.plcoding.echojournal.R

val Icons.Filled.Pause: ImageVector
    @Composable
    get() = ImageVector.vectorResource(R.drawable.pause)
val Icons.Filled.Mic: ImageVector
    @Composable
    get() = ImageVector.vectorResource(R.drawable.microphone)

val Icons.Filled.HashTag: ImageVector
    @Composable
    get() = ImageVector.vectorResource(R.drawable.hashtag)