package com.duskencodings.autologs.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity?.showKeyboard() {
  if (this == null) return
  (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
      ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity?.hideKeyboard() {
  if (this == null) return

  (getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { imm ->

    if (imm.isAcceptingText && currentFocus != null) {
      imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    } else {
      // long about way...
      val r = Rect()
      val contentView = findViewById<View>(android.R.id.content)
      contentView.getWindowVisibleDisplayFrame(r)
      val screenHeight = contentView.rootView.height

      // r.bottom is the position above soft keyboard or device button.
      // if keyboard is shown, the r.bottom is smaller than that before.
      val keyboardHeight = screenHeight - r.bottom

      // // 0.15 ratio is probably enough to determine keyboard height
      if (keyboardHeight > screenHeight * 0.15) {
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
      } else {
        // ignoring
      }
    }
  }
}