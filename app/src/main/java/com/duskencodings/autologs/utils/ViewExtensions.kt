package com.duskencodings.autologs.utils

import android.view.View
import android.widget.TextView

fun TextView.setTextOrHide(text: String?) {
  setText(text)
  visible = !text.isNullOrBlank()
}

var View.visible
  get() = visibility == View.VISIBLE
  set(visible) {
    visibility = if (visible) View.VISIBLE else View.GONE
  }