package com.duskencodings.autologs.log

import android.util.Log

class Logger {
  companion object {
    fun i(tag: String, message: String) {
      Log.i(tag, message)
    }

    fun d(tag: String, message: String) {
      Log.d(tag, message)
    }

    fun e(tag: String, message: String) {
      Log.e(tag, message)
    }

    fun v(tag: String, message: String) {
      Log.v(tag, message)
    }

    fun wtf(tag: String, message: String) {
      Log.wtf(tag, message)
    }
  }
}