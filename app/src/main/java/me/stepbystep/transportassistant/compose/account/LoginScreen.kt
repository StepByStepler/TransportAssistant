package me.stepbystep.transportassistant.compose.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.compose.util.CustomTextField
import me.stepbystep.transportassistant.util.asMutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun MainActivity.LoginScreen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        val loginValue = mutableStateOf("")
        CustomTextField(
            value = loginValue.asMutableProperty(),
            label = "Логин",
            modifier = Modifier.padding(top = 250.dp),
            keyboardType = KeyboardType.Text
        )

        Button(
            onClick = {
                login.set(loginValue.value.trim())
                needsReset = true
            },
            modifier = centerWithBottomPadding()
                .padding(top = 50.dp)
        ) {
            Text(text = "Войти", color = Color.Green)
        }
    }
}