package me.stepbystep.transportassistant.compose.stats

import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.OpenedPopupMenu
import me.stepbystep.transportassistant.R
import me.stepbystep.transportassistant.compose.util.ColumnPopupMenu
import me.stepbystep.transportassistant.data.FuelData
import me.stepbystep.transportassistant.data.MileageData
import me.stepbystep.transportassistant.util.centerWithBottomPadding
import org.joda.time.Instant
import org.joda.time.Weeks
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.javaType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.days

private const val DATA_COUNT = 5

val loadedMileageData = mutableStateOf<Array<MileageData>?>(null)
private val loadedFuelData = mutableStateOf<Array<FuelData>?>(null)

@Composable
fun ColumnScope.StatisticsButton(activity: MainActivity) {
    Button(
        onClick = {
            activity.openedMenu = OpenedPopupMenu.Statistics
        },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Открыть статистику")
    }
}

fun MainActivity.loadStatistics(login: String) {
    loadData(loadedMileageData, "/mileageData", login)
    loadData(loadedFuelData, "/fuelData", login)
}

@Composable
fun MainActivity.StatisticsMenu() {
    fun <T> Array<T>.dataForLast(
        duration: Duration,
        timestamp: T.() -> Long,
        difference: T.() -> Number,
    ): Int {
        val currentMillis = System.currentTimeMillis()
        val lastTimestamp = currentMillis - duration.inMilliseconds
        return takeLastWhile { it.timestamp() >= lastTimestamp }
            .sumByDouble { it.difference().toDouble() }
            .toInt()
    }

    val mileageData = loadedMileageData.value ?: return
    val fuelData = loadedFuelData.value ?: return

    val lastMileage = mileageData.dataForLast(30.days, MileageData::timestamp, MileageData::difference)
    val lastFuel = fuelData.dataForLast(30.days, FuelData::timestamp, FuelData::amount)
    val lastFuelUsage = when (lastMileage) {
        0 -> 0.0
        else -> lastFuel * 100.0 / lastMileage
    }
    val roundedLastFuelUsage = (lastFuelUsage * 100.0).roundToInt() / 100.0

    ColumnPopupMenu(type = OpenedPopupMenu.Statistics) {
        CenteredDarkGrayText(text = "Пробег (30 дней): $lastMileage")
        CenteredDarkGrayText(text = "Расход топлива (30 дней): $lastFuel")
        CenteredDarkGrayText(text = "Средний расход топлива (30 дней): $roundedLastFuelUsage")

        StatisticsGraph(mileageData, fuelData)
    }
}

@Composable
private fun ColumnScope.StatisticsGraph(
    mileageData: Array<MileageData>,
    fuelData: Array<FuelData>,
) {
    val mileagePerWeek = mileageData.selectDataPerWeek({ timestamp }, { difference })
    val fuelPerWeek = fuelData.selectDataPerWeek({ timestamp }, { amount })
    val usagePerWeek = fuelPerWeek.zip(mileagePerWeek)
        .map {
            if (it.second == 0)
                0
            else
                (it.first * 100.0 / it.second).toInt()
        }

    val tableHeight = 150.dp
    val tableHeightPx = with(LocalDensity.current) { tableHeight.roundToPx() }
    AndroidView(
        modifier = centerWithBottomPadding()
            .background(Color.Gray.copy(alpha = 0.5f))
            .size(400.dp, tableHeight)
            .align(Alignment.CenterHorizontally),
        factory = { ctx ->
            val layout = TableLayout(ctx).apply {
                isStretchAllColumns = true
                isShrinkAllColumns = true
                dividerDrawable = ContextCompat.getDrawable(ctx, R.color.black)
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            }

            val allData = listOf(getDateStamps(), mileagePerWeek, fuelPerWeek, usagePerWeek)
            val names = listOf("Дата", "Пробег", "Расход", "Средний расход")

            fun textView(text: String) = TextView(ctx).also {
                it.text = text
                it.setTextColor(android.graphics.Color.BLACK)
                it.setTypeface(null, Typeface.BOLD)
                it.gravity = Gravity.CENTER_HORIZONTAL
            }

            for ((index, dataRow) in allData.withIndex()) {
                val row = TableRow(ctx).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    dividerDrawable = ContextCompat.getDrawable(ctx, R.color.black)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                    addView(textView(names[index]))

                    dataRow.forEach { data ->
                        addView(textView(data.toString()))
                    }
                    minimumHeight = tableHeightPx / allData.size

                }

                layout.addView(row)
            }

            layout
        }
    )
}

private fun getDateStamps(): List<String> {
    fun Int.withLeadingZero(): String = when {
        this >= 10 -> this.toString()
        else -> "0$this"
    }

    return ((DATA_COUNT - 1) downTo 0).map { index ->
        val calendar = Calendar.getInstance()
        calendar[Calendar.WEEK_OF_MONTH] -= index
        val day = calendar[Calendar.DAY_OF_MONTH].withLeadingZero()
        val month = (calendar[Calendar.MONTH] + 1).withLeadingZero()

        "$day.$month"
    }
}

@Composable
private fun ColumnScope.CenteredDarkGrayText(text: String) {
    Text(text = text, color = Color.DarkGray, modifier = centerWithBottomPadding(10.dp))
}

private inline fun <reified T> MainActivity.loadData(
    state: MutableState<Array<T>?>,
    path: String,
    login: String,
) {
    httpClient.get(path, mapOf("login" to login)) {
        val typeToken = typeOf<Array<T>?>().javaType
        state.value = gson.fromJson(it, typeToken)
    }
}

private fun <T> Array<T>.selectDataPerWeek(
    getTimestamp: T.() -> Long,
    getDifference: T.() -> Number,
): List<Int> {
    val result = MutableList(DATA_COUNT) { 0.0 }
    if (isEmpty()) return result.map { it.toInt() }

    val currentInstant = Instant.now()

    for (data in reversed()) {
        val instant = Instant.ofEpochMilli(data.getTimestamp())
        val differenceWeeks = Weeks.weeksBetween(instant, currentInstant).weeks
        if (differenceWeeks >= DATA_COUNT) break

        result[result.lastIndex - differenceWeeks] += data.getDifference().toDouble()
    }

    println(result)

    return result.map { it.toInt() }
}