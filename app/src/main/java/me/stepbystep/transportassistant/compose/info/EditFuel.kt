package me.stepbystep.transportassistant.compose.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.*
import me.stepbystep.transportassistant.compose.util.ButtonSelectionGroup
import me.stepbystep.transportassistant.compose.util.CustomTextField
import me.stepbystep.transportassistant.compose.util.PopupMenu
import me.stepbystep.transportassistant.data.FuelData
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Composable
fun ColumnScope.EditFuelButton(activity: MainActivity) {
    Button(
        onClick = {
            activity.openedMenu = OpenedPopupMenu.EditFuel
        },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Добавить топливо")
    }
}

@Composable
fun MainActivity.EditFuel() {
    val activeFuelType = mutableStateOf<FuelType?>(null)
    val fuelTextField = mutableStateOf("")
    var fuelState by mutableStateOf(ColoredText.default())

    val fuelAmount = hashMapOf<FuelType, Double>()

    PopupMenu(type = OpenedPopupMenu.EditFuel) {
        Column(modifier = Modifier.padding(30.dp)) {
            Row(modifier = centerWithBottomPadding()) {
                ButtonSelectionGroup(activeItem = activeFuelType, allItems = FuelType.values()) {
                    Text(text = it.displayName)
                }
            }
            CustomTextField(value = fuelTextField.asMutableProperty(), label = "литров")
            Button(
                onClick = {
                    val fuelType = activeFuelType.value ?: run {
                        fuelState = "Выберите тип топлива".colored(Color.Red)
                        return@Button
                    }

                    val newValue = fuelTextField.value.trim()
                    val newAmount = newValue.replace(',', '.').toDoubleOrNull()
                    fuelState = when {
                        newAmount == null -> "Введите число".colored(Color.Red)
                        newAmount <= 0 -> "Введите положительное число".colored(Color.Red)
                        else -> {
                            addFuelData(FuelData(System.currentTimeMillis(), newAmount, fuelType))
                            fuelAmount[fuelType] = (fuelAmount[fuelType] ?: 0.0) + newAmount
                            "Топливо успешно добавлено".colored(Color.Green)
                        }
                    }
                },
                modifier = centerWithBottomPadding()
            ) {
                Text(text = "Добавить")
            }
            Text(
                text = fuelState.text,
                color = fuelState.color,
                modifier = centerWithBottomPadding()
            )

            fuelAmount.forEach { (fuelType, amount) ->
                if (amount == 0.0) return@forEach
                Text(text = "${fuelType.displayName}: $amount литров", color = Color.Yellow)
            }
        }
    }
}

private fun MainActivity.addFuelData(fuelData: FuelData) {
    httpClient.post("/addFuel", mapOf(
        "json" to gson.toJson(fuelData)
    ))
}