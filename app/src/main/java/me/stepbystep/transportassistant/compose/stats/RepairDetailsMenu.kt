package me.stepbystep.transportassistant.compose.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.stepbystep.transportassistant.MainActivity
import me.stepbystep.transportassistant.OpenedPopupMenu
import me.stepbystep.transportassistant.compose.util.PopupMenu
import me.stepbystep.transportassistant.data.RepairData
import me.stepbystep.transportassistant.repair.RepairDetail
import me.stepbystep.transportassistant.util.Colors
import me.stepbystep.transportassistant.util.centerWithBottomPadding
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

private val repairDetails = mutableStateOf<Map<RepairDetail, RepairData>?>(null)

fun MainActivity.loadRepairDetails(login: String) {
    httpClient.get("/latestRepairs", mapOf("login" to login)) {
        val typeToken = typeOf<Map<String, RepairData>>().javaType
        repairDetails.value = gson.fromJson(it, typeToken)
        println(repairDetails.value)
    }
}

@Composable
fun ColumnScope.RepairButton(activity: MainActivity) {
    Button(
        onClick = {
            activity.openedMenu = OpenedPopupMenu.RepairDetails
        },
        modifier = centerWithBottomPadding()
    ) {
        Text(text = "Открыть запчасти")
    }
}

@Composable
fun MainActivity.RepairDetailsMenu() {
    val loadedMileageData = loadedMileageData.value ?: return
    val totalMileage = loadedMileageData.sumBy { it.difference }
    val repairDetails = repairDetails.value ?: return

    PopupMenu(type = OpenedPopupMenu.RepairDetails) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 30.dp)
                .heightIn(max = 300.dp)
                .verticalScroll(rememberScrollState())
        ) {
            println("totalMileage = $totalMileage")
            val sortedDetails = repairDetails.toList().sortedByDescending {
                totalMileage - it.second.mileage
            }

            for ((_, repairData) in sortedDetails) {
                val mileageDiff = totalMileage - repairData.mileage
                val diffPart = mileageDiff.toDouble() / repairData.detail.requiredMileage
                println(diffPart)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(8.dp),
                ) {
                    Box(
                        modifier = Modifier.background(
                            when {
                                diffPart >= 1.0 -> Colors.BrightRed
                                diffPart >= 0.65 -> Colors.BrightYellow
                                else -> Colors.BrightGreen
                            }
                        ).clip(RoundedCornerShape(10.dp)),
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 10.dp)
                        ) {
                            val detailName = repairData.detail.displayName
                            Text(
                                text = "$detailName: $mileageDiff км. назад",
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}