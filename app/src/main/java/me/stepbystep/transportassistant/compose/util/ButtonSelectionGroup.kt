package me.stepbystep.transportassistant.compose.util

import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T : Any> ButtonSelectionGroup(
    activeItem: MutableState<T?>,
    allItems: Array<T>,
    modifier: Modifier = Modifier,
    isActive: (T) -> Boolean = { true },
    buttonContent: @Composable (T) -> Unit,
) {
    for (item in allItems) {
        if (isActive(item)) {
            val color = if (activeItem.value == item) Color.Gray else Color.White
            Button(
                onClick = {
                    activeItem.value = item
                },
                modifier = modifier.background(color),
            ) {
                buttonContent(item)
            }
        }
    }
}