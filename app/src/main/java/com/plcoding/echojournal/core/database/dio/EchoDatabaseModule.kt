package com.plcoding.echojournal.core.database.dio

import androidx.room.Room
import com.plcoding.echojournal.core.database.EchoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<EchoDatabase> {
        Room.databaseBuilder(
            androidContext(),
            EchoDatabase::class.java,
            "echo.db",
        ).build()
    }
    single {
        get<EchoDatabase>().echoDao
    }
}