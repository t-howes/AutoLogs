package sample.thowes.autoservice.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.today(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
}
