package com.lightningkite.koolui.android

import android.graphics.drawable.Drawable
import com.lightningkite.koolui.image.ImageWithOptions

private val NumberRegex = Regex("\\d+\\.?\\d*")
fun ImageWithOptions.android(): Drawable = this.image.drawable
