package com.duskencodings.autologs.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.showToast(@StringRes textRes: Int, duration: Int = Toast.LENGTH_LONG) {
  if (this == null) return
  showToast(getString(textRes), duration)
}

fun Context?.showToast(text: String?, duration: Int = Toast.LENGTH_LONG) {
  if (this == null || text == null) return
  Toast.makeText(this, text, duration).show()
}