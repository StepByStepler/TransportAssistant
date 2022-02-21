package me.stepbystep.transportassistant.data

import me.stepbystep.transportassistant.FuelType

data class FuelData(
    val timestamp: Long,
    val amount: Double,
    val type: FuelType,
)