package com.duskencodings.autologs.models

data class SpendingBreakdown(val maintenanceCosts: Double,
                             val modsCost: Double,
                             val totalCost: Double = maintenanceCosts + modsCost) {

  fun isEmpty(): Boolean = maintenanceCosts == 0.0 && modsCost == 0.0
}