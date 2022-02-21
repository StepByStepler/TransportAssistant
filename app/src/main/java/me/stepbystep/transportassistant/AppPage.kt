package me.stepbystep.transportassistant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppPage(val displayName: String, val icon: ImageVector) {
    Update("Обновить", Icons.Filled.Edit),
    Statistics("Статистика", Icons.Filled.Info),
    Account("Аккаунт", Icons.Filled.AccountBox);
}