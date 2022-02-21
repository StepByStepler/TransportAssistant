package me.stepbystep.transportassistant.compose.info

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun ColumnScope.MainInfoScreen(activity: MainActivity, login: String) {
    LoginInfo(login)
    EditMileage(activity)
    EditFuelButton(activity)
    EditRepairButton(activity)
}

@Composable
fun ColumnScope.LoginInfo(login: String) {
    Text(
        text = "Логин: $login",
        modifier = centerWithBottomPadding().padding(top = 30.dp),
        color = Color.Green
    )
}