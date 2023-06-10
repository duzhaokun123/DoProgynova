package io.github.duzhaokun123.doprogynova

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.github.duzhaokun123.doprogynova.utils.doOnce
import io.github.duzhaokun123.doprogynova.utils.runIO
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

class QSTileServer : TileService() {
    val aDayDao by lazy { (application as Application).db.aDayDao() }

    override fun onStartListening() {
        super.onStartListening()
        runIO {
            update()
        }
    }

    override fun onClick() {
        super.onClick()
        doOnce(aDayDao) {
            qsTile.subtitle = "吃太多了"
            qsTile.updateTile()
        }
        runIO {
            delay(200)
            update()
        }
    }

    private fun update() {

        val data = LocalDate.now().let { it.year * 10000 + it.monthValue * 100 + it.dayOfMonth }
        val aDay = aDayDao.getByData(data)
        if (aDay == null) {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.subtitle = "未吃"
        } else {
            val aDo = aDay.do3 ?: aDay.do2 ?: aDay.do1 ?: aDay.do0
            if (aDo == null) {
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.subtitle = "未吃"
            } else {
                val doTime = LocalTime.of(aDo / 100, aDo % 100)
                val now = LocalTime.now()
                if (now.isAfter(doTime.plusHours(5).plusMinutes(30)))
                    qsTile.state = Tile.STATE_INACTIVE
                else
                    qsTile.state = Tile.STATE_ACTIVE
                qsTile.subtitle = "上次 $doTime"
            }
        }
        qsTile.updateTile()
    }
}