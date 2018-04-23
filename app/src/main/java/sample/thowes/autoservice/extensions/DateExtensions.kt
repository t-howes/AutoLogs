package sample.thowes.autoservice.extensions

import java.text.SimpleDateFormat
import java.util.*

const val MONTH_DAY_YEAR = "MM/dd/yyyy"
const val MONTH_DAY_YEAR_TIME = "$MONTH_DAY_YEAR hh:mm a"

fun Calendar.today(): String {
  return SimpleDateFormat(MONTH_DAY_YEAR, Locale.getDefault()).format(time)
}

fun Calendar.now(): String {
  return SimpleDateFormat(MONTH_DAY_YEAR_TIME, Locale.getDefault()).format(time)
}

fun String?.asDate(): Date? {
  if (this == null) return null
  val formatter = SimpleDateFormat(MONTH_DAY_YEAR, Locale.getDefault())
  return formatter.parse(this)
}