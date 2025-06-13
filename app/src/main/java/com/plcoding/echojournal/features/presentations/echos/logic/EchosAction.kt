package com.plcoding.echojournal.features.presentations.echos.logic

import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.TrackSizeInfo

enum class EchoFilterChip {
    MOODS, TOPICS
}


sealed interface EchosAction {
    data object OnMoodChipClick : EchosAction
    data object OnDismissMoodDropDown : EchosAction
    data class OnFilterByMoodClick(val moodUi: MoodUi) : EchosAction
    data object OnTopicChipClick : EchosAction
    data object OnDismissTopicDropDown : EchosAction
    data class OnFilterByTopicClick(val topic: String) : EchosAction
    data object OnFabClick : EchosAction
    data object OnFabLongClick : EchosAction
    data object OnSettingsClick : EchosAction
    data object OnPauseRecordingClick : EchosAction
    data object OnResumeRecordingClick : EchosAction
    data object OnCompleteRecording : EchosAction
    data object OnPauseAudioClick : EchosAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : EchosAction
    data class OnRemoveFilters(val filterType: EchoFilterChip) : EchosAction
    data class OnPlayEchoClick(val echoId: Int) : EchosAction
    data object OnAudioPermissionGranted : EchosAction
    data object OnCancelRecording : EchosAction
    data object OnLoadEchos : EchosAction
}