package com.plcoding.echojournal.features.presentations.create_echo.logic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.echojournal.app.navigation.NavigationRoutes
import com.plcoding.echojournal.features.data.data_source.RoomEchoDataSource
import com.plcoding.echojournal.features.domain.audio.AudioPlayer
import com.plcoding.echojournal.features.domain.data_source.Echo
import com.plcoding.echojournal.features.domain.data_source.Mood
import com.plcoding.echojournal.features.domain.recording.RecordingStorage
import com.plcoding.echojournal.features.presentations.models.MoodUi
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import com.plcoding.echojournal.features.presentations.util.AmplitudeNormalizer
import com.plcoding.echojournal.features.presentations.util.toRecordingDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.time.Duration

class CreateEchoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recordingStorage: RecordingStorage,
    private val audioPlayer: AudioPlayer,
    private val roomEchoDataSource: RoomEchoDataSource
) : ViewModel() {

    private val route = savedStateHandle.toRoute<NavigationRoutes.CreateEcho>()
    private val recordingDetails = route.toRecordingDetails()

    private val eventChannel = Channel<CreateEchoEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false


    private var durationJob: Job? = null

    private val _state = MutableStateFlow(
        CreateEchoState(
            playbackTotalDuration = recordingDetails.duration
        )
    )
    val state = _state.onStart {
        if (!hasLoadedInitialData) {

            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CreateEchoState()
    )





    fun onAction(action: CreateEchoAction) {
        when (action) {


            is CreateEchoAction.OnTitleTextChange -> {
                _state.update {
                    it.copy(
                        titleText = action.text,
                        canSaveEcho = it.titleText.isNotBlank(),
                    )
                }
            }

            is CreateEchoAction.OnNoteTextChange -> {
                _state.update {
                    it.copy(noteText = action.text)
                }
            }


            is CreateEchoAction.OnMoodClick -> {
                onMoodClick(action.moodUi)
            }


            CreateEchoAction.OnPlayAudioClick -> {
                if (state.value.playbackState == PlaybackState.PAUSED) {
                    audioPlayer.resume()
                } else {
                    audioPlayer.play(
                        filePath = recordingDetails.filePath ?: throw IllegalArgumentException(
                            "File path can't be null"
                        ),
                        onComplete = {
                            _state.update {
                                it.copy(
                                    playbackState = PlaybackState.STOPPED,
                                    durationPlayed = Duration.ZERO,


                                    )
                            }
                        }
                    )
                    durationJob = audioPlayer.activeTrack.filterNotNull().onEach { track ->
                        _state.update {
                            it.copy(
                                playbackState = if (track.isPlaying) PlaybackState.PLAYING else PlaybackState.STOPPED,
                                durationPlayed = track.durationPlayed
                            )
                        }
                    }.launchIn(viewModelScope)
                }
            }


            CreateEchoAction.OnSaveClick -> {
                if (recordingDetails.filePath == null) {
                    return
                }
                viewModelScope.launch {
                    val savedFilePath = recordingStorage.savePersistently(
                        tempFilePath = recordingDetails.filePath
                    )
                    if (savedFilePath == null) {
                        eventChannel.send(CreateEchoEvent.FailedToSaveFile)
                        return@launch
                    }
                    val currentState = state.value
                    val echo = Echo(
                        mood = currentState.mood?.let { Mood.valueOf(it.name) }
                            ?: throw IllegalStateException("Mood must be set before saving echo."),
                        title = currentState.titleText.trim(),
                        topics = currentState.topics,
                        audioFilePath = savedFilePath,
                        note = currentState.noteText,
                        audioAmplitudes = recordingDetails.amplitudes,
                        audioPlaybackLength = currentState.playbackTotalDuration,
                        recordedAt = Instant.now()
                    )
                    roomEchoDataSource.insertEcho(echo)
                    eventChannel.send(CreateEchoEvent.FileSaveSuccessfully)
                }
            }

            CreateEchoAction.OnSelectMoodClick -> {
                showMoodSelectionSheet()
            }


            CreateEchoAction.OnDismissMoodSelector -> {
                _state.update {
                    it.copy(
                        showMoodSelector = false
                    )
                }
            }

            is CreateEchoAction.OnAddTopicTextChange -> {

            }

            CreateEchoAction.OnConfirmMood -> {
                onConfirmMood()
            }

            CreateEchoAction.OnCreateNewTopicClick -> {

            }

            CreateEchoAction.OnDismissTopicSuggestions -> {

            }

            CreateEchoAction.OnPauseAudioClick -> audioPlayer.pause()

            is CreateEchoAction.OnRemoveTopicClick -> {

            }

            is CreateEchoAction.OnTopicClick -> {

            }

            is CreateEchoAction.OnTrackSizeAvailable -> {
                viewModelScope.launch(Dispatchers.Default) {
                    val finalAmplitudes = AmplitudeNormalizer.normalize(
                        sourceAmplitudes = recordingDetails.amplitudes,
                        barWidth = action.trackSizeInfo.barWidth,
                        spacing = action.trackSizeInfo.spacing,
                        trackWidth = action.trackSizeInfo.trackWidth
                    )
                    _state.update {
                        it.copy(
                            playbackAmplitudes = finalAmplitudes
                        )
                    }
                }
            }

            CreateEchoAction.OnDismissConfirmLeaveDialog -> {
                _state.update {
                    it.copy(
                        showConfirmLeaveDialog = false
                    )
                }
            }

            CreateEchoAction.OnCancelClick,
            CreateEchoAction.OnNavigateBackClick,
            CreateEchoAction.OnGoBack -> {
                _state.update {
                    it.copy(
                        showConfirmLeaveDialog = true
                    )
                }
            }
        }
    }


    private fun showMoodSelectionSheet() {
        _state.update {
            it.copy(
                showMoodSelector = true
            )
        }
    }

    private fun onConfirmMood() {
        _state.update {
            it.copy(
                mood = it.selectedMood,

                showMoodSelector = false
            )
        }
    }

    private fun onMoodClick(mood: MoodUi) {
        _state.update {
            it.copy(
                selectedMood = mood
            )
        }
    }


}