package io.github.duzhaokun123.doprogynova.utils

import io.github.duzhaokun123.doprogynova.room.ADay
import io.github.duzhaokun123.doprogynova.room.ADayDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

fun runIO(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.IO, block = block)

val LocalDate.isToday
    get() = LocalDate.now().let {
        it.year == year && it.monthValue == monthValue && it.dayOfMonth == dayOfMonth
    }

fun doOnce(aDayDao: ADayDao, onDoToMuch: () -> Unit) {
    runIO {
        val data = LocalDate.now()
            .let { it.year * 10000 + it.monthValue * 100 + it.dayOfMonth }
        val time = LocalTime.now().let { it.hour * 100 + it.minute }
        var aDay = aDayDao.getByData(data) ?: ADay(data)
        when {
            aDay.do0 == null -> aDay = aDay.copy(do0 = time)
            aDay.do1 == null -> aDay = aDay.copy(do1 = time)
            aDay.do2 == null -> aDay = aDay.copy(do2 = time)
            aDay.do3 == null -> aDay = aDay.copy(do3 = time)
            else -> onDoToMuch()
        }
        aDayDao.upsert(aDay)
    }
}