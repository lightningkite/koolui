package com.lightningkite.koolui.android.access

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File


/**
 * Requests permission to use the camera, takes a picture using the camera app, and puts it into the [requestedUri].
 */
fun ActivityAccess.intentCameraRaw(
    requestedUri: Uri,
    callback: (Uri?) -> Unit
) {
    requestPermission(Manifest.permission.CAMERA) {
        if (it) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, requestedUri)
            startIntent(intent) { code, result ->
                if (code == Activity.RESULT_OK) callback(result?.data ?: requestedUri)
                else callback(null)
            }
        } else {
            callback(null)
        }
    }
}

/**
 * Requests permission to use the camera, takes a picture using the camera app, and puts it into a
 * temporary file using the given [fileProviderAuthority].
 *
 * The default parameter for [fileProviderAuthority] is your app's package name plus ".fileprovider".
 *
 * The default parameter for [folder] is in the cache directory under the folder "images".  Your file
 * provider's XML *must* have whatever folder you decide to use.
 */
fun ActivityAccess.intentCamera(
    fileProviderAuthority: String = context.packageName + ".fileprovider",
    folder: File = File(context.cacheDir, "images").also { it.mkdirs() },
    callback: (Uri?) -> Unit
) {

    val file = folder
        .let { File.createTempFile("imageWithOptions", ".jpg", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    intentCameraRaw(file, callback)
}

/**
 * Requests permission to read external storage and shows the system gallery to select an image.
 * Returns the image URI selected in the callback.
 */
fun ActivityAccess.intentGallery(
    callback: (Uri?) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) callback(result?.data)
                else callback(null)
            }
        } else {
            callback(null)
        }
    }
}
