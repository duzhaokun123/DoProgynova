package io.github.duzhaokun123.doprogynova

import androidx.room.Room
import io.github.duzhaokun123.doprogynova.room.AppDatabase

lateinit var application: Application

class Application: android.app.Application() {
    init {
        application = this
    }

    val db: AppDatabase by lazy {
        Room.databaseBuilder(application, AppDatabase::class.java, "days.db").build()
    }
}