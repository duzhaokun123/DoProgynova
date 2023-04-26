package io.github.duzhaokun123.doprogynova.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ADay(
    @PrimaryKey
    val data: Int = 0,
    @ColumnInfo
    val do0: Int? = null,
    @ColumnInfo
    val do1: Int? = null,
    @ColumnInfo
    val do2: Int? = null,
    @ColumnInfo
    val do3: Int? = null,
)