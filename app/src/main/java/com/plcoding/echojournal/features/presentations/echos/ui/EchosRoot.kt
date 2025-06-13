package com.plcoding.echojournal.features.presentations.echos.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.presentation.IsAppInForeground
import com.plcoding.echojournal.core.util.ObserveAsEvents
import com.plcoding.echojournal.features.domain.recording.RecordingDetails
import com.plcoding.echojournal.features.presentations.echos.logic.EchosAction
import com.plcoding.echojournal.features.presentations.echos.logic.EchosEvent
import com.plcoding.echojournal.features.presentations.echos.logic.EchosViewModel
import com.plcoding.echojournal.features.presentations.models.RecordingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun EchosRoot(
    onNavigateToCreateEcho: (RecordingDetails) -> Unit,
    viewModel: EchosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onAction(EchosAction.OnAudioPermissionGranted)
        }
    }

    val isAppInForeground by IsAppInForeground()
    LaunchedEffect(isAppInForeground, state.recordingState) {
        if (state.recordingState == RecordingState.NORMAL && !isAppInForeground) {
            viewModel.onAction(EchosAction.OnPauseRecordingClick)
        }
    }

    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is EchosEvent.RequestAudioPermission -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            is EchosEvent.RecordingTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_LONG
                ).show()
            }

            is EchosEvent.OnDoneRecording -> {
                onNavigateToCreateEcho(event.details)
            }
        }
    }

    EchosScreen(
        state = state,
        onAction = viewModel::onAction
    )
}


