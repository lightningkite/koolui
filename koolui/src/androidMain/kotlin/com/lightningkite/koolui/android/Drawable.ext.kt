package com.lightningkite.koolui.android

import android.graphics.drawable.Drawable
import com.lightningkite.koolui.image.ImageWithSizing

private val NumberRegex = Regex("\\d+\\.?\\d*")
fun ImageWithSizing.android(): Drawable = this.image.drawable
