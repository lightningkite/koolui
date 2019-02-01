package com.lightningkite.koolui.image

import com.lightningkite.koolui.resources.Resources


expect suspend fun Resources.getImage(filename: String): Image
