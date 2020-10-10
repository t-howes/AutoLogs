package com.duskencodings.autologs.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.duskencodings.autologs.R
import com.duskencodings.autologs.base.BaseActivity
import com.duskencodings.autologs.base.BaseFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 * Responsible for launching the intent to take a picture or pick from gallery.
 * This way either an Activity can startForResult() or a Fragment can call and
 * not worry about the result delating up to the parent Activity. Also this is
 * for keeping track of which ImageView position to load
 * the image/file into after taking a picture or selecting from gallery.
 */
interface PhotoSelectionHandler {
  val photoContext: BaseActivity
  var tempFile: File?

  fun onTakePhotoClicked() {
    photoContext.addSub(
      RxPermissions(photoContext)
        .request(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        .subscribe({ granted ->
          if (granted) {
            try {
              tempFile = photoContext.createImageFile(USER_IMAGE.format(System.currentTimeMillis()))

              val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
              // check if there's a camera activity (idk why there wouldn't be...)
              if (intent.resolveActivity(photoContext.packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile!!.getUriFromProvider(photoContext))
                launchPhotoIntent(intent, TAKE_PICTURE_REQUEST_CODE)
              }
            } catch (e: Exception) {
              e.printStackTrace()
              photoContext.showToast(R.string.error_starting_camera)
            }
          } else {
            photoContext.showToast(R.string.error_photo_permissions)
          }
        }, {
          photoContext.showToast(R.string.error_taking_photo)
        })
    )
  }

  /**
   * looks silly, but we need to cast to the proper type and call startActivityForResult
   */
  fun launchPhotoIntent(intent: Intent, requestCode: Int) {
    when (this) {
      is BaseFragment -> this.startActivityForResult(intent, requestCode)
      is BaseActivity -> this.startActivityForResult(intent, requestCode)
    }
  }

  fun onPickPhotoFromGalleryClicked() {
    photoContext.addSub(
      RxPermissions(photoContext)
        .request(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        .subscribe({ granted ->
          if (granted) {
            startGallerySelection()
          } else {
            photoContext.showToast(R.string.error_photo_permissions)
          }
        }, {
          photoContext.showToast(R.string.error_selecting_photo)
        })
    )
  }

  private fun startGallerySelection() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    launchPhotoIntent(intent, CHOOSE_PHOTO_REQUEST_CODE)
  }

  fun onGallerySelectResult(data: Intent?) {
    if (data == null) {
      photoContext.showToast(R.string.error_selecting_photo)
      return
    }

    try {
      data.data?.let { selectedImageUri ->
        // content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253A4c86c33b-cf8a-4461-914f-a4af7b09d58f/ORIGINAL/NONE/image%2Fjpeg/32579110
        val fullPicturePath = selectedImageUri.determinePath(photoContext) ?: run {
          photoContext.showToast(R.string.error_selecting_photo)
          return@let
        }
        val parsedFileName = fullPicturePath.substring(fullPicturePath.lastIndexOf("/") + 1, fullPicturePath.lastIndexOf("."))
        val destinationFile = photoContext.createImageFile(parsedFileName)

        selectedImageUri.copyFileStream(photoContext, destinationFile)

        startCrop(destinationFile)
      }
    } catch (e: Exception) {
      photoContext.showToast(R.string.error_selecting_photo)
    }
  }

  fun startCrop(file: File) {
    val sourceUri = file.toUri()
    val destinationFile = photoContext.createImageFile(file.nameWithoutExtension + "_cropped")
    val outputUri = destinationFile.toUri()

    val crop = UCrop.of(sourceUri, outputUri)
        .withAspectRatio(1f, 1f)
//        .withMaxResultSize(300, 300)

    when (this) {
      is BaseActivity -> crop.start(photoContext)
      is BaseFragment -> crop.start(photoContext, this)
    }
  }

  fun handleCropResult(result: Intent?): Uri? {
    return result?.let {
      UCrop.getOutput(it)?.also { uri ->
        uri.path?.let { path ->
          File(path).also { file ->
            tempFile = file
          }
        }
      }
    }
  }

  fun onCropError(data: Intent?) {
    data?.let {
      UCrop.getError(data)?.let { cropError ->
        photoContext.onError(cropError)
      } ?: photoContext.onError(Exception("Failed to crop image and we have no intent data"))
    } ?: photoContext.onError(Exception("Failed to crop image and we have no intent data"))
  }
}

const val USER_IMAGE = "image%s_"
const val CHOOSE_PHOTO_REQUEST_CODE = 1122
const val TAKE_PICTURE_REQUEST_CODE = 1234