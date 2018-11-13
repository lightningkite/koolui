package com.lightningkite.koolui.android

import android.graphics.drawable.Drawable
import com.larvalabs.svgandroid.SVG
import com.larvalabs.svgandroid.SVGBuilder
import com.lightningkite.koolui.image.Image

private val NumberRegex = Regex("\\d+\\.?\\d*")
private val SVGCache = HashMap<String, SVG>()
fun Image.android(): Drawable = this.displayable.drawable
