package com.plcoding.echojournal.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plcoding.echojournal.features.presentations.create_echo.ui.CreateEchoRoot
import com.plcoding.echojournal.features.presentations.echos.ui.EchosRoot
import com.plcoding.echojournal.features.presentations.util.toCreateEchoRoute


@Composable
fun NavigationRoot(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Echos
    ) {
        composable<NavigationRoutes.Echos> {
            EchosRoot(
                onNavigateToCreateEcho = {
                    navController.navigate(it.toCreateEchoRoute())
                }
            )
        }
        composable<NavigationRoutes.CreateEcho> {
            CreateEchoRoot(
                onConfirmLeave = navController::navigateUp
            )
        }
    }
}