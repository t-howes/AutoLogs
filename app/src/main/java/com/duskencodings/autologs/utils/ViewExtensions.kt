package com.duskencodings.autologs.utils

import android.view.View
import android.widget.TextView

fun TextView.setTextOrHide(text: String?) {
  setText(text)
  visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
}