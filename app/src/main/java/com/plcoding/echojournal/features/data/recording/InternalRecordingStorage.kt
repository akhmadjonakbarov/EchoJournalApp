package com.plcoding.echojournal.features.data.recording

import android.content.Context
import com.plcoding.echojournal.features.domain.recording.RecordingStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.time.temporal.ChronoUnit

class InternalRecordingStorage(
    private val context: Context,
    private val applicationScope: CoroutineScope
) : RecordingStorage {

    override suspend fun savePersistently(tempFilePath: String): String? {
        val tempFile = File(tempFilePath)
        if (!tempFile.exists()) {
            Timber.e("The temp file does not exist")
            return null
        }
        return withContext(Dispatchers.IO) {
            try {
                val savedFile = generateSavedFile()
                tempFile.copyTo(savedFile)
                savedFile.absolutePath
            } catch (e: IOException) {
                Timber.e(e)
                null
            } finally {
                withContext(NonCancellable) {
                    cleanUpTemporaryFiles()
                }
            }
        }
    }

    override suspend fun cleanUpTemporaryFiles() {
        withContext(Dispatchers.IO) {
            context.cacheDir.listFiles()
                ?.filter { it.name.startsWith(RecordingStorage.TEMP_FILE_PREFIX) }
                ?.forEach { file ->
                    file.delete()
                }
        }
    }

    private fun generateSavedFile(): File {
        val timestamp = java.time.Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        return File(
            context.filesDir,
            "${RecordingStorage.PERSISTENT_FILE_PREFIX}_$timestamp.${RecordingStorage.FILE_EXT}"
        )
    }
}