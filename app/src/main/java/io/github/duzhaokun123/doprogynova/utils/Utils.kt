package io.github.duzhaokun123.doprogynova.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

fun runIO(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.IO, block = block)

val LocalDate.isToday
    get() = LocalDate.now().let {
        it.year == year && it.monthValue == monthValue && it.dayOfMonth == dayOfMonth
    }