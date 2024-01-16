package com.example.roomtodo

import android.app.Application
import androidx.room.Room


class RoomApplication: Application() {
    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "todos"
        ).build()
    }
}