package com.duskencodings.autologs.models

data class SpendingBreakdown(val replacementCosts: Double,
                             val modsCost: Double,
                             val serviceCosts: Double) {

  fun isEmpty(): Boolean = replacementCosts == 0.0 && modsCost == 0.0 && serviceCosts == 0.0
}