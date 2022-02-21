package me.stepbystep.transportassistant.compose.info

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.ColoredText
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.colored
import me.stepbystep.transportassistant.compose.stats.refreshSelectedDriverData
import me.stepbystep.transportassistant.compose.util.CustomTextField
import me.stepbystep.transportassistant.data.MileageData
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun ColumnScope.EditMileage(activity: MainActivity) {
    val (currentMileage) = mutableStateOf(
        activity.mileageData.sumOf { it.difference }
    )

    val mileageTextFieldValue = remember { mutableStateOf("") }
    var mileageUpdateState by remember { mutableStateOf(ColoredText.default()) }

    Text(
        text = "Текущий пробег: $currentMileage",
        modifier = centerWithBottomPadding().padding(top = 15.dp),
        color = Color.Green
    )
    CustomTextField(
        value = mileageTextFieldValue.asMutableProperty(),
        label = "пробег"
    )
    Button(
        onClick = {
            println("mileage: ${mileageTextFieldValue.value}")
            val newValue = mileageTextFieldValue.value.trim().toIntOrNull()

            mileageUpdateState = when {
                newValue == null ->
                    "Введите число".colored(Color.Red)
                newValue <= currentMileage ->
                    "Пробег должен быть больше, чем уже указанный".colored(Color.Red)
                else -> {
                    println("currentMileage: $currentMileage")
                    val mileageDifference = newValue - currentMileage
                    val timestamp = when (currentMileage) {
                        0 -> 0L
                        else -> System.currentTimeMillis()
                    }
                    activity.addMileageData(MileageData(timestamp, mileageDifference))
                    activity.refreshSelectedDriverData()
                    "Пробег успешно обновлен".colored(Color.Green)
                }
            }
        },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Обновить пробег")
    }
    Text(
        text = mileageUpdateState.text,
        color = mileageUpdateState.color,
        modifier = centerWithBottomPadding()
    )
}

private fun MainActivity.addMileageData(mileageData: MileageData) {
    this.mileageData += mileageData
    httpClient.post("/addMileage", mapOf(
        "json" to gson.toJson(mileageData)
    ))
}