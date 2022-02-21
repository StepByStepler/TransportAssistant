package me.stepbystep.transportassistant.compose.account

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.compose.info.LoginInfo
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun ColumnScope.AccountScreen(activity: MainActivity, login: String) {
    LoginInfo(login)
    ExitButton(activity)
}

@Composable
fun ColumnScope.ExitButton(activity: MainActivity) {
    Button(
        modifier = centerWithBottomPadding(),
        onClick = {
            activity.login.set(null)
            activity.needsReset = true
        }
    ) {
        Text(text = "Выйти")
    }
}