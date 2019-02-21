package com.lightningkite.koolui.android

import android.graphics.drawable.Drawable
import com.larvalabs.svgandroid.SVG
import com.lightningkite.koolui.image.ImageWithSizing

private val NumberRegex = Regex("\\d+\\.?\\d*")
private val SVGCache = HashMap<String, SVG>()
fun ImageWithSizing.android(): Drawable = this.image.drawable
