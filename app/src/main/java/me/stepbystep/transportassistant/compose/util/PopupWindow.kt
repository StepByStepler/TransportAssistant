package me.stepbystep.transportassistant.compose.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.OpenedPopupMenu
import me.stepbystep.transportassistant.util.centerWithBottomPadding

@Composable
fun MainActivity.PopupMenu(
    type: OpenedPopupMenu,
    topPadding: Dp = 150.dp,
    children: @Composable (ColumnScope.() -> Unit)
) {
    if (openedMenu == type) {
        Column(
            modifier = Modifier.fillMaxSize().background(
                brush = SolidColor(Color.DarkGray),
                alpha = 0.8f
            ).clickable {
                openedMenu = null
            }
        ) {
            // nothing
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                elevation = 16.dp,
                color = Color.White,
                modifier = centerWithBottomPadding()
                    .padding(top = topPadding)
                    .clickable {} // nothing on click
            ) {
                children()
            }
        }
    }
}

@Composable
fun MainActivity.ColumnPopupMenu(type: OpenedPopupMenu, children: @Composable (ColumnScope.() -> Unit)) {
    PopupMenu(type = type) {
        Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 30.dp)) {
            children()
        }
    }
}