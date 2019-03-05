package com.duskencodings.autologs.extensions

import java.text.NumberFormat

fun Double?.formatMoney(): String {
  if (this == null) return "$0.00"
  return NumberFormat.getCurrencyInstance().format(this)
}