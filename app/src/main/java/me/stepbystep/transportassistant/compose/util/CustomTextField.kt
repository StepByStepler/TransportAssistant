package me.stepbystep.transportassistant.compose.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import me.stepbystep.transportassistant.util.MutableProperty
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun ColumnScope.CustomTextField(
    value: MutableProperty<String>,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    val focusController = LocalFocusManager.current
    TextField(
        value = value.get(),
        onValueChange = { text ->
            value.set(text)
        },
        modifier = centerWithBottomPadding().then(modifier),//.align(Alignment.CenterHorizontally),
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        keyboardActions = KeyboardActions(onDone = { focusController.clearFocus() }),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Yellow,
            unfocusedIndicatorColor = Color.White,
        ),
//        inactiveColor = Color.White,
//        activeColor = Color.Yellow,
//        onTextInputStarted = {
//            lastKeyboardController = it
//        }
    )
}