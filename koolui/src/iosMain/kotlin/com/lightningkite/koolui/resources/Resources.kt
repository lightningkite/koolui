package com.lightningkite.koolui.resources

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.toByteArray
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import platform.Foundation.*

actual object Resources {

    actual suspend fun getString(
            filename: String
    ): String {
        val path = NSBundle.mainBundle.pathForResource(filename.substringBeforeLast('.'), filename.substringAfterLast('.'))!!
        return NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)!!
    }

    actual suspend fun getByteArray(
            filename: String
    ): ByteArray {
        val path = NSBundle.mainBundle.pathForResource(filename.substringBeforeLast('.'), filename.substringAfterLast('.'))!!
        return NSData.dataWithContentsOfFile(path)!!.toByteArray()
    }

    actual suspend fun getImage(filename: String): Image = Image.fromByteArray(getByteArray(filename))

}
