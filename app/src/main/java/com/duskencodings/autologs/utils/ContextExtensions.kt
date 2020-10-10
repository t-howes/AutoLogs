package com.duskencodings.autologs.utils

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Context?.showToast(@StringRes textRes: Int, duration: Int = Toast.LENGTH_LONG) {
  if (this == null) return
  showToast(getString(textRes), duration)
}

fun Context?.showToast(text: String?, duration: Int = Toast.LENGTH_LONG) {
  if (this == null || text == null) return
  Toast.makeText(this, text, duration).show()
}

fun Context.notificationManager() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// region File extensions
fun Context.createImageFile(name: String): File {
  return File.createTempFile(name, ".jpg", getImageFilesDirectory())
}

fun Context.getImageFilesDirectory(): File {
  val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
  if  (!dir.exists()) {
    dir.mkdir()
  }

  return dir
}
//endregion