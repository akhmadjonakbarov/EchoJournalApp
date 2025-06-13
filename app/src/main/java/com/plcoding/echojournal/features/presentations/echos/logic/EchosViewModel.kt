package com.plcoding.echojournal.features.presentations.echos.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.echojournal.features.presentations.models.MoodUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn


import com.plcoding.echojournal.R
import com.plcoding.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.plcoding.echojournal.core.util.UiText
import com.plcoding.echojournal.features.data.data_source.RoomEchoDataSource
import com.plcoding.echojournal.features.domain.audio.AudioPlayer
import com.plcoding.echojournal.features.domain.data_source.EchoDataSource
import com.plcoding.echojournal.features.domain.recording.VoiceRecorder
import com.plcoding.echojournal.features.presentations.models.EchoUi
import com.plcoding.echojournal.features.presentations.models.MoodChipContent
import com.plcoding.echojournal.features.presentations.models.PlaybackState
import com.plcoding.echojournal.features.presentations.models.RecordingState
import com.plcoding.echojournal.features.presentations.models.TrackSizeInfo
import com.plcoding.echojournal.features.presentations.util.AmplitudeNormalizer
import com.plcoding.echojournal.features.presentations.util.toEchoUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class EchosViewModel(
    private val voiceRecorder: VoiceRecorder,
    private val audioPlayer: AudioPlayer,
    private val echoDataSource: EchoDataSource
) : ViewModel() {

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }

    private var hasLoadedInitialData = false


    private val playingEchoId = MutableStateFlow<Int?>(null)
    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicFilters = MutableStateFlow<List<String>>(emptyList())
    private val audioTrackSizeInfo = MutableStateFlow<TrackSizeInfo?>(null)

    private val eventChannel = Channel<EchosEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EchosState())
    val state = _state.onStart {
        if (!hasLoadedInitialData) {
            observeFilters()
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = EchosState()
    )


    private val echos = echoDataSource.observeEchos().onEach { echos ->
            _state.update {
                it.copy(
                    hasEchosRecorded = echos.isNotEmpty(), isLoadingData = false
                )
            }
        }.combine(audioTrackSizeInfo) { echos, trackSizeInfo ->
            if (trackSizeInfo != null) {
                echos.map { echo ->
                    echo.copy(
                        audioAmplitudes = AmplitudeNormalizer.normalize(
                            sourceAmplitudes = echo.audioAmplitudes,
                            trackWidth = trackSizeInfo.trackWidth,
                            barWidth = trackSizeInfo.barWidth,
                            spacing = trackSizeInfo.spacing
                        )
                    )
                }
            } else echos
        }.flowOn(Dispatchers.Default)

    init {
        onAction(EchosAction.OnLoadEchos)
    }

    fun onAction(action: EchosAction) {
        when (action) {

            EchosAction.OnFabClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(

                    )
                }
            }

            EchosAction.OnFabLongClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(

                    )
                }
            }

            EchosAction.OnSettingsClick -> {}

            is EchosAction.OnRemoveFilters -> {
                when (action.filterType) {
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList() }
                    EchoFilterChip.TOPICS -> selectedTopicFilters.update { emptyList() }
                }
            }

            EchosAction.OnTopicChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.TOPICS
                    )
                }
            }

            EchosAction.OnMoodChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.MOODS
                    )
                }
            }

            EchosAction.OnDismissTopicDropDown, EchosAction.OnDismissMoodDropDown -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = null
                    )
                }
            }

            is EchosAction.OnFilterByMoodClick -> {
                toggleMoodFilter(action.moodUi)
            }

            is EchosAction.OnFilterByTopicClick -> {
                toggleTopicFilter(action.topic)
            }

            is EchosAction.OnPlayEchoClick -> {
                val selectedEcho =
                    state.value.echos.values.flatten().first { action.echoId == it.id }
                val activeTrack = audioPlayer.activeTrack.value
                val isNewEcho = playingEchoId.value != action.echoId
                val isSameEchoIsPlayingFromBeginning =
                    action.echoId == playingEchoId.value && activeTrack != null && activeTrack.durationPlayed == Duration.ZERO
                when {
                    isNewEcho || isSameEchoIsPlayingFromBeginning -> {
                        playingEchoId.update {
                            action.echoId
                        }
                        audioPlayer.stop()
                        audioPlayer.play(filePath = selectedEcho.audioFilePath) {
                            _state.update {
                                it.copy(
                                    echos = it.echos.mapValues { (_, echos) ->
                                        echos.map { echo ->
                                            echo.copy(
                                                playbackTotalDuration = Duration.ZERO
                                            )
                                        }
                                    })
                            }
                            playingEchoId.update { null }

                        }
                    }

                    else -> audioPlayer.resume()
                }
            }

            is EchosAction.OnTrackSizeAvailable -> {
                audioTrackSizeInfo.update {
                    action.trackSizeInfo
                }

            }

            is EchosAction.OnAudioPermissionGranted -> {
                startRecording()
            }

            EchosAction.OnPauseAudioClick -> {

            }

            EchosAction.OnPauseRecordingClick -> pauseRecording()

            EchosAction.OnCancelRecording -> cancelRecording()

            EchosAction.OnCompleteRecording -> stopRecording()

            EchosAction.OnResumeRecordingClick -> resumeRecording()
            EchosAction.OnLoadEchos -> {
                combine(
                    echos, playingEchoId, audioPlayer.activeTrack
                ) { echos, playingEchoId, activeTrack ->
                    if (playingEchoId == null || activeTrack == null) {
                        return@combine echos.map { it.toEchoUi() }
                    }

                    echos.map { echo ->
                        if (echo.id == playingEchoId) {
                            echo.toEchoUi(
                                currentPlaybackDuration = activeTrack.durationPlayed,
                                playbackState = if (activeTrack.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
                            )
                        } else echo.toEchoUi()
                    }
                }.groupByRelativeDate().onEach { groupedEchos ->
                        _state.update {
                            it.copy(
                                echos = groupedEchos
                            )
                        }
                    }.flowOn(Dispatchers.Default).launchIn(viewModelScope)

            }
        }
    }

    private fun requestAudioPermission() = viewModelScope.launch {
        eventChannel.send(EchosEvent.RequestAudioPermission)
    }

    private fun pauseRecording() {
        voiceRecorder.pause()
        _state.update {
            it.copy(
                recordingState = RecordingState.PAUSED
            )
        }
    }

    private fun completePlayBack() {}

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingState = RecordingState.NORMAL
            )
        }
    }

    private fun cancelRecording() {
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING,

                )
        }
        voiceRecorder.cancel()
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING
            )
        }

        val recordingDetails = voiceRecorder.recordingDetails.value
        viewModelScope.launch {
            if (recordingDetails.duration < MIN_RECORD_DURATION) {
                eventChannel.send(EchosEvent.RecordingTooShort)
            } else {
                eventChannel.send(EchosEvent.OnDoneRecording(recordingDetails))
            }
        }
    }

    private fun Flow<List<EchoUi>>.groupByRelativeDate(): Flow<Map<UiText, List<EchoUi>>> {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val today = LocalDate.now()
        return map { echos ->
            echos.groupBy { echo ->
                    LocalDate.ofInstant(
                        echo.recordedAt, ZoneId.systemDefault()
                    )
                }.mapValues { (_, echos) ->
                    echos.sortedByDescending { it.recordedAt }
                }.toSortedMap(compareByDescending { it }).mapKeys { (date, _) ->
                    when (date) {
                        today -> UiText.StringResource(R.string.today)
                        today.minusDays(1) -> UiText.StringResource(R.string.yesterday)
                        else -> UiText.Dynamic(date.format(formatter))
                    }
                }
        }
    }

    private fun startRecording() {
        _state.update {
            it.copy(
                recordingState = RecordingState.NORMAL
            )
        }
        voiceRecorder.start()

        voiceRecorder.recordingDetails.distinctUntilChangedBy { it.duration }.map { it.duration }
            .onEach { duration ->
                _state.update {
                    it.copy(
                        recordingElapsedDuration = duration
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMoods ->
            if (moodUi in selectedMoods) {
                selectedMoods - moodUi
            } else {
                selectedMoods + moodUi
            }
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicFilters.update { selectedTopics ->
            if (topic in selectedTopics) {
                selectedTopics - topic
            } else {
                selectedTopics + topic
            }
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicFilters, selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topics = it.topics.map { selectableTopic ->
                    Selectable(
                        item = selectableTopic.item,
                        selected = selectedTopics.contains(selectableTopic.item)
                    )
                },
                    moods = MoodUi.entries.map {
                        Selectable(
                            item = it, selected = selectedMoods.contains(it)
                        )
                    },
                    hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                    hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                    topicChipTitle = selectedTopics.deriveTopicsToText(),
                    moodChipContent = selectedMoods.asMoodChipContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if (this.isEmpty()) {
            return MoodChipContent()
        }

        val icons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }

        return when (size) {
            1 -> MoodChipContent(
                iconsRes = icons, title = moodNames.first()
            )

            2 -> MoodChipContent(
                iconsRes = icons, title = UiText.Combined(
                    format = "%s, %s", uiTexts = moodNames.toTypedArray()
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = icons, title = UiText.Combined(
                        format = "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2).toTypedArray()
                    )
                )
            }
        }
    }
}