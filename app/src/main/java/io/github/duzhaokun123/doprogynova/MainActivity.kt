package io.github.duzhaokun123.doprogynova

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.duzhaokun123.doprogynova.room.ADay
import io.github.duzhaokun123.doprogynova.ui.theme.DoProgynovaTheme
import io.github.duzhaokun123.doprogynova.utils.isToday
import io.github.duzhaokun123.doprogynova.utils.runIO
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val dataFormatter = DateTimeFormatter.ofPattern("MM-dd")
    private val aDayDao by lazy { (application as Application).db.aDayDao() }
    private val exportCsvResponse =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            uri ?: return@registerForActivityResult
            val outputStream =
                contentResolver.openOutputStream(uri) ?: return@registerForActivityResult
            runIO {
                val sb = StringBuilder()
                sb.append("日期,吃药时间1,吃药时间2,吃药时间3,吃药时间4\n")
                aDayDao.getAll().forEach { day ->
                    val date =
                        LocalDate.of(day.data / 10000, day.data % 10000 / 100, day.data % 100)
                    val do0 = day.do0?.let { LocalTime.of(it / 100, it % 100) }
                    val do1 = day.do1?.let { LocalTime.of(it / 100, it % 100) }
                    val do2 = day.do2?.let { LocalTime.of(it / 100, it % 100) }
                    val do3 = day.do3?.let { LocalTime.of(it / 100, it % 100) }
                    sb.append("$date,$do0,$do1,$do2,$do3\n")
                }
                outputStream.writer().use {
                    it.write(sb.toString())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        setContent {
            var onDoToMuchDialog by remember { mutableStateOf(false) }
            DoProgynovaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
                        DataCard(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        DoButton(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        ) {
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
                                    else -> onDoToMuchDialog = true
                                }
                                if (onDoToMuchDialog.not())
                                    aDayDao.upsert(aDay)
                            }
                        }
                        IconButton(
                            onClick = { exportCsvResponse.launch("${LocalDate.now()}.csv") },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(imageVector = Icons.Default.Launch, contentDescription = "export")
                        }
                    }
                    if (onDoToMuchDialog)
                        OnDoToMuchDialog {
                            onDoToMuchDialog = false
                        }
                }
            }
        }
        runIO {
            val data = LocalDate.now().let { it.year * 10000 + it.monthValue * 100 + it.dayOfMonth }
            val aDay = aDayDao.getByData(data)
            if (aDay == null) {
                aDayDao.upsert(ADay(data))
            }
        }
    }

    @Composable
    fun DataCard(modifier: Modifier) {
        Box(modifier = modifier) {
            val days = aDayDao.getAllFlow().collectAsState(initial = listOf())
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                LazyRow(reverseLayout = true) {
                    items(days.value.reversed()) { day ->
                        val date =
                            LocalDate.of(day.data / 10000, day.data % 10000 / 100, day.data % 100)
                        val do0 = day.do0?.let { LocalTime.of(it / 100, it % 100) }
                        val do1 = day.do1?.let { LocalTime.of(it / 100, it % 100) }
                        val do2 = day.do2?.let { LocalTime.of(it / 100, it % 100) }
                        val do3 = day.do3?.let { LocalTime.of(it / 100, it % 100) }
                        ADayCard(date, do0, do1, do2, do3)
                    }
                }
            }
        }
    }

    @Composable
    fun DoButton(modifier: Modifier, onDo: () -> Unit = { }) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            Button(
                onClick = onDo,
                modifier = Modifier.size(180.dp, 100.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.ado),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
    }

    @Composable
    fun OnDoToMuchDialog(onDismissRequest: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            title = { Text("吃太多了吧") },
        )
    }

    @Composable
    fun ADayCard(
        date: LocalDate,
        do0: LocalTime?,
        do1: LocalTime?,
        do2: LocalTime?,
        do3: LocalTime?
    ) {
        Surface(color = if (date.isToday) MaterialTheme.colorScheme.tertiary else Color.Transparent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    date.format(dataFormatter),
                    modifier = Modifier
                        .height(70.dp)
                        .padding(top = 10.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    do0?.toString() ?: "未吃",
                    modifier = Modifier.height(60.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    do1?.toString() ?: "未吃",
                    modifier = Modifier.height(60.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    do2?.toString() ?: "未吃",
                    modifier = Modifier.height(60.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    do3?.toString() ?: "未吃",
                    modifier = Modifier.height(60.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
