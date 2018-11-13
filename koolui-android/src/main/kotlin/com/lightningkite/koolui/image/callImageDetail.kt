package com.lightningkite.koolui.image

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.lightningkite.kommunicate.*
import com.lightningkite.koolui.ApplicationAccess

actual suspend fun HttpClient.callImageDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody,
    headers: Map<String, List<String>>
): HttpResponse<Displayable> = callByteArrayDetail(
    url, method, body, headers
).copy {
    @Suppress("DEPRECATION")
    Displayable(BitmapDrawable(BitmapFactory.decodeByteArray(it, 0, it.size)))
}
