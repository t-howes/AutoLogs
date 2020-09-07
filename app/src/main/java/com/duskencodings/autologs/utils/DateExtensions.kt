package com.duskencodings.autologs.utils

import com.duskencodings.autologs.utils.log.Logger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

const val MONTH_DAY_YEAR = "MM/dd/yyyy"
const val MONTH_DAY_YEAR_TIME = "$MONTH_DAY_YEAR hh:mm a"

fun now(): LocalDate = LocalDate.now()
fun nowFormatted(): String = now().formatted()

fun LocalDate.formatted(): String = format(DateTimeFormatter.ofPattern(MONTH_DAY_YEAR))

fun String?.toDateOrNull(): LocalDate? {
  return if (this == null) null else try {
    LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
  } catch (e: DateTimeParseException) {
    LocalDate.parse(this, DateTimeFormatter.ofPattern(MONTH_DAY_YEAR))
  } catch (e: Exception) {
    Logger.e("STRING TO DATE", "Failed to parse date from: $this.", e)
    null
  }
}