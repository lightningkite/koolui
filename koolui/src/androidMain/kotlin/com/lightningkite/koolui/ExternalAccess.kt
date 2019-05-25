package com.lightningkite.koolui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.lightningkite.koolui.android.access.intentGallery
import com.lightningkite.koolui.android.access.startIntent
import com.lightningkite.kommon.string.*

actual object ExternalAccess {

    actual fun openUri(uri: com.lightningkite.kommon.string.Uri) {
        ApplicationAccess.access?.context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri.string)))
    }

    actual fun saveToChosenFile(name: String, contentType: MediaTypeWithDescription, data: ByteArray, alwaysOpenDialog: Boolean, callback: (succeeded: Boolean) -> Unit) {
        ApplicationAccess.access?.apply {
            //            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
//                if (it) {
            val getIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            getIntent.addCategory(Intent.CATEGORY_OPENABLE)
            getIntent.type = contentType.mediaType.string.toLowerCase()

            this.startIntent(getIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    try {
                        result?.data?.let { uri ->
                            context.contentResolver
                                    .openOutputStream(uri, "w")
                                    .use { it.write(data) }
                            callback(true)
                        } ?: callback(false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(false)
                    }
                } else callback(false)
//                } else {
//                    callback(null)
//                }
//            }
            }
        }
    }

    actual fun loadFromChosenFile(contentTypes: List<MediaTypeAcceptWithDescription>, callback: (data: ByteArray?) -> Unit) {
        ApplicationAccess.access?.apply {
            //            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
//                if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = contentTypes.joinToString(";") { it.mediaTypeAccept.string.toLowerCase() }

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = contentTypes.joinToString(";") { it.mediaTypeAccept.string.toLowerCase() }

            val chooserIntent = Intent.createChooser(getIntent, "Select File")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    try {
                        result?.data?.let { uri ->
                            val stuff = context.contentResolver
                                    .openInputStream(uri)
                                    .use { it.readBytes() }
                            callback(stuff)
                        } ?: callback(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(null)
                    }
                } else callback(null)
            }
//                } else {
//                    callback(null)
//                }
//            }
        }
    }
}