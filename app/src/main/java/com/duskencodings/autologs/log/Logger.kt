package com.duskencodings.autologs.log

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.duskencodings.autologs.BuildConfig

class Logger {
  companion object {
    fun i(tag: String, message: String) {
      if (BuildConfig.DEBUG) {
        Log.i(tag, message)
      }
    }

    fun d(tag: String, message: String) {
      if (BuildConfig.DEBUG) {
        Log.d(tag, message)
      }
    }

    fun e(tag: String, message: String, exception: Throwable) {
      if (BuildConfig.DEBUG) {
        Log.e(tag, message)
      } else {
        Crashlytics.logException(exception)
      }
    }

    fun v(tag: String, message: String) {
      if (BuildConfig.DEBUG) {
        Log.v(tag, message)
      }
    }

    fun wtf(tag: String, message: String) {
      if (BuildConfig.DEBUG) {
        Log.wtf(tag, message)
      }
    }
  }
}