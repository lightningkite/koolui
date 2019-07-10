package com.lightningkite.koolui

import com.lightningkite.kommon.string.MediaTypeAccept
import com.lightningkite.kommon.string.MediaTypeAcceptWithDescription
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.kommon.string.Uri
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.reinterpret
import platform.CoreFoundation.CFStringRef
import platform.CoreServices.UTTypeCreatePreferredIdentifierForTag
import platform.CoreServices.kUTTagClassMIMEType
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

actual object ExternalAccess {
    actual fun openUri(uri: Uri) {
        println("Opening URI $uri")
        val url = NSURL.URLWithString(uri.string)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        } else {
            //TODO: Alert the user?
        }
    }

    actual fun saveToChosenFile(
            name: String,
            contentType: MediaTypeWithDescription,
            data: ByteArray,
            alwaysOpenDialog: Boolean,
            callback: (succeeded: Boolean) -> Unit
    ) {
        val vc = UIDocumentBrowserViewController()
        vc.allowsDocumentCreation = true
        vc.delegate = object : NSObject(), UIDocumentBrowserViewControllerDelegateProtocol {
            override fun documentBrowser(
                    controller: UIDocumentBrowserViewController,
                    didRequestDocumentCreationWithHandler: (NSURL?, UIDocumentBrowserImportMode) -> Unit
            ) {
//                if (it) {
//                    didRequestDocumentCreationWithHandler(doc.fileURL, UIDocumentBrowserImportMode.UIDocumentBrowserImportModeMove)
//                } else {
                didRequestDocumentCreationWithHandler(null, UIDocumentBrowserImportMode.UIDocumentBrowserImportModeNone)
//                }
            }

            override fun documentBrowser(controller: UIDocumentBrowserViewController, didPickDocumentURLs: List<*>) {
                (didPickDocumentURLs as List<NSURL>).firstOrNull()?.let {
                    val doc = MyDocument(it)
                    doc.data = data
                    doc.saveToURL(it, UIDocumentSaveOperation.UIDocumentSaveForOverwriting, callback)
                }
            }
        }
        ApplicationAccess.baseVC?.presentViewController(
                viewControllerToPresent = vc,
                animated = true,
                completion = { }
        )
    }

    actual fun loadFromChosenFile(
            contentTypes: List<MediaTypeAcceptWithDescription>,
            callback: (data: ByteArray?) -> Unit
    ) {
        val vc = UIDocumentBrowserViewController(
                forOpeningFilesWithContentTypes = contentTypes.map { it.mediaTypeAccept.toUTI() }
        )
        vc.allowsDocumentCreation = false
        vc.delegate = object : NSObject(), UIDocumentBrowserViewControllerDelegateProtocol {
            override fun documentBrowser(controller: UIDocumentBrowserViewController, didPickDocumentURLs: List<*>) {
                (didPickDocumentURLs as List<NSURL>).firstOrNull()?.let {
                    val doc = MyDocument(it)
                    doc.openWithCompletionHandler {
                        callback(doc.data)
                    }
                }
            }
        }
        ApplicationAccess.baseVC?.presentViewController(
                viewControllerToPresent = vc,
                animated = true,
                completion = { }
        )
    }


    class MyDocument(url: NSURL) : UIDocument(url) {

        var data: ByteArray = byteArrayOf()

        override fun contentsForType(typeName: String, error: CPointer<ObjCObjectVar<NSError?>>?): Any? {
            return data.toNSData()
        }

        override fun loadFromContents(contents: Any, ofType: String?, error: CPointer<ObjCObjectVar<NSError?>>?): Boolean {
            val asData = contents as NSData
            data = asData.toByteArray()
            return true
        }
    }
}

fun MediaTypeAccept.toUTI(): String {
    val ref: CFStringRef = CFBridgingRetain(this.string)!!.reinterpret()
    val x = UTTypeCreatePreferredIdentifierForTag(
            inTagClass = kUTTagClassMIMEType,
            inTag = ref,
            inConformingToUTI = null
    )
    return CFBridgingRelease(x) as String
}
