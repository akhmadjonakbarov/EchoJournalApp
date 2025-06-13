package com.plcoding.echojournal.features.presentations.echos.logic

import com.plcoding.echojournal.features.domain.recording.RecordingDetails

interface EchosEvent {
    data object RequestAudioPermission : EchosEvent
    data object RecordingTooShort : EchosEvent
    data class OnDoneRecording(val details: RecordingDetails) : EchosEvent
}