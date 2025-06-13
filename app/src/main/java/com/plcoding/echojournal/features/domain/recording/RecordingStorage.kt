package com.plcoding.echojournal.features.domain.recording

interface RecordingStorage {
    suspend fun savePersistently(tempFilePath: String): String?
    suspend fun cleanUpTemporaryFiles()

    companion object {
        const val FILE_EXT = "mp3"
        const val PERSISTENT_FILE_PREFIX = "recording"
         const val TEMP_FILE_PREFIX = "temp_recording"
    }

}