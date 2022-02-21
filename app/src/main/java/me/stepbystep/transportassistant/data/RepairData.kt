package me.stepbystep.transportassistant.data

import me.stepbystep.transportassistant.repair.RepairDetail

data class RepairData(
    val mileage: Int,
    val detail: RepairDetail,
)