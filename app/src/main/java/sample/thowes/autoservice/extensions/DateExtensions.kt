package sample.thowes.autoservice.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.today(): String {
  return SimpleDateFormat("MM/dd/yyy", Locale.getDefault()).format(time)
}

fun Calendar.now(): String {
  return SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault()).format(time)
}
