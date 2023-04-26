package io.github.duzhaokun123.doprogynova.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ADay::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun aDayDao(): ADayDao
}