package com.plcoding.echojournal.features.presentations.create_echo.logic

sealed interface CreateEchoEvent {
    data object FailedToSaveFile : CreateEchoEvent
    data object FileSaveSuccessfully : CreateEchoEvent
}