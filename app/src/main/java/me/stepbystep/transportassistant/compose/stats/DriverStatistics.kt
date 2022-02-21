package me.stepbystep.transportassistant.compose.stats

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.ColoredText
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.colored
import me.stepbystep.transportassistant.compose.util.CustomTextField
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding

private var selectedDriver by mutableStateOf("")

@Composable
fun ColumnScope.DriverStatistics(activity: MainActivity) {
    if (selectedDriver.isEmpty()) {
        selectedDriver = activity.login.get().orEmpty()
    }
    val nameInField = remember { mutableStateOf(selectedDriver) }
    var searchState by remember { mutableStateOf<ColoredText?>(null) }

    fun updateSelectedName(selectedName: String) {
        selectedDriver = selectedName
        activity.refreshSelectedDriverData()
        searchState = "Водитель успешно найден".colored(Color.Green)
    }

    updateSelectedName(selectedDriver)

    CustomTextField(
        value = nameInField.asMutableProperty(),
        label = "Логин",
        modifier = Modifier.padding(top = 50.dp),
        keyboardType = KeyboardType.Text
    )

    Button(
        onClick = {
            val ownerLogin = activity.login.get()
            val selectedName = nameInField.value.trim()

            if (ownerLogin == "admin" || ownerLogin == selectedName) {
                updateSelectedName(selectedName)
            } else {
                searchState = "Вы не можете просматривать чужую статистику".colored(Color.Red)
            }
        },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Найти")
    }

    searchState?.let { label ->
        Text(text = label.text, color = label.color, modifier = centerWithBottomPadding())
    }

    if (selectedDriver.isNotEmpty()) {
        StatisticsButton(activity)
        RepairButton(activity)
    }
}

fun MainActivity.refreshSelectedDriverData() {
    val login = selectedDriver
    if (login.isNotEmpty()) {
        println("refreshing data for: $login")
        loadStatistics(login)
        loadRepairDetails(login)
    }
}