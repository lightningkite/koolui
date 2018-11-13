package com.lightningkite.koolui.resources

import com.lightningkite.kommunicate.*
import com.lightningkite.koolui.ApplicationAccess

actual object Resources {
    actual suspend fun getString(
        filename: String
    ): String =
        HttpClient.callString(url = ApplicationAccess.appPath + "/resources/" + filename, method = HttpMethod.GET)

    actual suspend fun getByteArray(
        filename: String
    ): ByteArray =
        HttpClient.callByteArray(url = ApplicationAccess.appPath + "/resources/" + filename, method = HttpMethod.GET)

}
