package com.plcoding.echojournal.core.database.echo

import androidx.room.TypeConverter
import com.plcoding.echojournal.features.domain.data_source.Mood
import com.plcoding.echojournal.features.presentations.models.MoodUi

class MoodTypeConverter {
    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(moodName: String): Mood {
        return Mood.valueOf(moodName)
    }
}