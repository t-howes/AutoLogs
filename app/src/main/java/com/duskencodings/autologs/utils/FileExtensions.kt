package com.duskencodings.autologs.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.duskencodings.autologs.utils.log.Logger
import com.yalantis.ucrop.util.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * copy contents from the given URI into the dest file
 */
@Throws(IOException::class)
fun Uri.copyFileStream(context: Context, destinationFile: File) {
  context.contentResolver.openInputStream(this).use { inputStream ->
    FileOutputStream(destinationFile).use { outputStream ->
      val buffer = ByteArray(1024)
      var length = 0

      while ({ length = inputStream!!.read(buffer); length }() > 0) {
        outputStream.write(buffer, 0, length)
      }
    }
  }
}

/**
 * This 'toUri()' method is used for starting the camera intent with
 * an output to this Uri.
 * If you use [toUri] for starting the camera, the camera will fail to start.
 */
fun File.getUriFromProvider(context: Context): Uri {
  return FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", this)
}

/**
 * Used for other Uri needs such as creating a source/destination Uri for cropping.
 * If you try to use [getUriFromProvider] in these cases, an exception is thrown for
 * file/uri not found.
 */
fun File.toUri(): Uri = Uri.fromFile(this)

fun Uri.determinePath(context: Context): String? {
  val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

  // DocumentProvider
  if (isKitKat && DocumentsContract.isDocumentUri(context, this)) {
    if (FileUtils.isExternalStorageDocument(this)) {
      val docId = DocumentsContract.getDocumentId(this)
      val split = docId.split(":")
      val type = split[0]

      if ("primary".equals(type, true)) {
        return "${Environment.getExternalStorageDirectory()}/${split[1]}"
      }

      // TODO handle non-primary volumes
    }
    // DownloadsProvider
    else if (FileUtils.isDownloadsDocument(this)) {
      val id = DocumentsContract.getDocumentId(this)

      if (!TextUtils.isEmpty(id)) {
        return try {
          val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
          FileUtils.getDataColumn(context, contentUri, null, null)
        } catch (e: NumberFormatException) {
          Logger.i("URI EXTENSIONS", e.localizedMessage ?: "Error occurred while determining Uri path.")
          null
        }
      }

    }
    // MediaProvider
    else if (FileUtils.isMediaDocument(this)) {
      val docId = DocumentsContract.getDocumentId(this)
      val split = docId.split(":")
      val type = split[0]
      var contentUri: Uri? = null

      when (type) {
        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
      }

      val selection = "_id=?"
      val selectionArgs = arrayOf(
          split[1]
      )

      return FileUtils.getDataColumn(context, contentUri, selection, selectionArgs)
    }
  }
  // MediaStore (and general)
  else if ("content".equals(scheme, true)) {
    // Return the remote address
    if (FileUtils.isGooglePhotosUri(this)) {
      return lastPathSegment
    }

    return FileUtils.getDataColumn(context, this, null, null)
  }
  // File
  else if ("file".equals(scheme, true)) {
    return path
  }

  return null
}