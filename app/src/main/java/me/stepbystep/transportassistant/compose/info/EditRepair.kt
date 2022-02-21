package me.stepbystep.transportassistant.compose.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.ColoredText
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.OpenedPopupMenu
import me.stepbystep.transportassistant.colored
import me.stepbystep.transportassistant.compose.util.CustomTextField
import me.stepbystep.transportassistant.compose.util.PopupMenu
import me.stepbystep.transportassistant.data.RepairData
import me.stepbystep.transportassistant.repair.RepairDetail
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun ColumnScope.EditRepairButton(activity: MainActivity) {
    Button(
        onClick = { activity.openedMenu = OpenedPopupMenu.EditRepair },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Обновить запчасти")
    }
}

@Composable
fun MainActivity.EditRepair() {
    PopupMenu(type = OpenedPopupMenu.EditRepair, topPadding = 80.dp) {
        Column(modifier = Modifier.padding(30.dp)) {
            PerformEditRepair(this@EditRepair)
        }
    }
}

@Composable
private fun ColumnScope.PerformEditRepair(activity: MainActivity) {
    val selectedDetail = remember { mutableStateOf<RepairDetail?>(RepairDetail.BrakePads2) }
    val repairSearchText = remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ColoredText.default()) }
    val mileageValue = remember { mutableStateOf(activity.mileageData.sumOf { it.difference }.toString()) }

    CustomTextField(value = mileageValue.asMutableProperty(), label = "Пробег во время замены")

    CustomTextField(
        value = repairSearchText.asMutableProperty(),
        label = "Поиск запчасти",
        keyboardType = KeyboardType.Text,
    )

    Column(modifier = centerWithBottomPadding()
        .height(150.dp)
        .verticalScroll(rememberScrollState())
    ) {
        for (item in RepairDetail.values()) {
            if (item.displayName.containsWithOrder(repairSearchText.value)) {
                val color = if (selectedDetail.value == item) Color.Gray else Color.White
                Button(
                    onClick = {
                        selectedDetail.value = item
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = color)
                ) {
                    item.displayName.HighlightedCharacters(other = repairSearchText.value)
                }
            }
        }
//        ButtonSelectionGroup(
//            activeItem = selectedDetail,
//            allItems = RepairDetail.values(),
//            modifier = Modifier.fillMaxWidth(),
//            isActive = { it.displayName.containsWithOrder(repairSearchText.value) }
//        ) {
//            it.displayName.HighlightedCharacters(other = repairSearchText.value)
//        }
    }

    if (status.text.isNotEmpty()) {
        Text(
            text = status.text,
            color = status.color,
            modifier = centerWithBottomPadding(),
        )
    }

    Button(
        modifier = centerWithBottomPadding(),
        onClick = {
            val mileage = mileageValue.value.toIntOrNull()

            if (mileage == null || mileage <= 0) {
                status = "В поле пробега должно быть натуральное число".colored(Color.Red)
                return@Button
            }

            val detail = selectedDetail.value
            if (detail == null) {
                status = "Выберите запчасть".colored(Color.Red)
                return@Button
            }

            val repairData = RepairData(mileage, detail)
            activity.addRepairData(repairData)
            status = "Вы успешно сохранили запчасть".colored(Color.Green)
        }
    ) {
        Text(text = "Сохранить")
    }
}

private fun String.containsWithOrder(other: String): Boolean {
    if (other.isEmpty()) return true

    var currentCharIndex = 0
    for (char in this) {
        if (char.equals(other[currentCharIndex], ignoreCase = true)) {
            currentCharIndex++
            if (currentCharIndex == other.length) {
                return true
            }
        }
    }

    return false
}

@Composable
private fun String.HighlightedCharacters(other: String) {
    if (other.isEmpty()) {
        Text(text = this)
        return
    }

    var currentCharIndex = 0
    for ((index, char) in this.withIndex()) {
        val color = when {
            char.equals(other[currentCharIndex], ignoreCase = true) -> {
                currentCharIndex++
                Color.Green
            }
            else -> Color.Unspecified
        }

        Text(text = char.toString(), color = color)

        if (currentCharIndex == other.length) {
            Text(text = substring(index + 1), color = Color.Unspecified)
            return
        }
    }
}

private fun MainActivity.addRepairData(repairData: RepairData) {
    httpClient.post("/addRepair", mapOf(
        "json" to gson.toJson(repairData)
    ))
}