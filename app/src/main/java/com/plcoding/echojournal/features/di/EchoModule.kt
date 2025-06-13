package com.plcoding.echojournal.features.di

import com.plcoding.echojournal.features.data.audio.AndroidAudioPlayer
import com.plcoding.echojournal.features.data.data_source.RoomEchoDataSource
import com.plcoding.echojournal.features.data.recording.AndroidVoiceRecorder
import com.plcoding.echojournal.features.data.recording.InternalRecordingStorage
import com.plcoding.echojournal.features.domain.audio.AudioPlayer
import com.plcoding.echojournal.features.domain.data_source.EchoDataSource
import com.plcoding.echojournal.features.domain.recording.RecordingStorage
import com.plcoding.echojournal.features.domain.recording.VoiceRecorder
import com.plcoding.echojournal.features.presentations.create_echo.logic.CreateEchoViewModel
import com.plcoding.echojournal.features.presentations.echos.logic.EchosViewModel

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


val echoModule = module {
    singleOf(::AndroidVoiceRecorder) bind VoiceRecorder::class
    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class
    singleOf(::RoomEchoDataSource) bind EchoDataSource::class
    viewModelOf(::EchosViewModel)
    viewModelOf(::CreateEchoViewModel)
}