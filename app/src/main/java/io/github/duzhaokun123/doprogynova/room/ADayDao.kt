package io.github.duzhaokun123.doprogynova.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ADayDao {
    @Query("SELECT * FROM ADay")
    fun getAllFlow(): Flow<List<ADay>>

    @Query("SELECT * FROM ADay")
    fun getAll(): List<ADay>

    @Query("SELECT * FROM ADay WHERE data >= :from AND data <= :to")
    fun getFromTo(from: Int, to: Int): List<ADay>

    @Query("SELECT * FROM ADay WHERE data = :data")
    fun getByData(data: Int): ADay?

    @Upsert
    fun upsert(aDay: ADay)

    @Delete
    fun delete(aDay: ADay)
}