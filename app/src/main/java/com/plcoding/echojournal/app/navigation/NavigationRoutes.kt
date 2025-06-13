package com.plcoding.echojournal.app.navigation

import kotlinx.serialization.Serializable
import kotlin.time.Duration

sealed interface NavigationRoutes {

    @Serializable
    data object Echos : NavigationRoutes

    @Serializable
    data class CreateEcho(
        val recordingPath: String,
        val duration: Long,
        val amplitudes: String
    ) : NavigationRoutes
}
