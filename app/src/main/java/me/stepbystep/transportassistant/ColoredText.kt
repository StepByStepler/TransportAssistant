package me.stepbystep.transportassistant

import androidx.compose.ui.graphics.Color

data class ColoredText(
    val text: String,
    val color: Color
) {
    companion object {
        fun default() = ColoredText("", Color.Red)
    }
}

fun String.colored(color: Color): ColoredText = ColoredText(this, color)