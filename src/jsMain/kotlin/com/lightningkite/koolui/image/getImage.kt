package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.resources.Resources

actual suspend fun Resources.getImage(filename: String): Displayable =
    HttpClient.callImage(url = ApplicationAccess.appPath + "/resources/" + filename, method = HttpMethod.GET)
