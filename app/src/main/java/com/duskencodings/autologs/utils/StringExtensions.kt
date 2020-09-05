package com.duskencodings.autologs.utils

import com.duskencodings.autologs.utils.log.Logger
import java.time.LocalDate

fun String.toDateOrNull(): LocalDate? {
  return try {
    LocalDate.parse(this)
  } catch (e: Exception) {
    Logger.e("STRING TO DATE", "Failed to parse date from: $this.", e)
    null
  }
}