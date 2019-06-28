package com.lightningkite.koolui.android

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.util.*

private val ViewDesiredHeight = WeakHashMap<View, Int>()
var View.desiredHeight: Int?
    get() = ViewDesiredHeight[this]
    set(value) {
        ViewDesiredHeight[this] = value
    }
private val ViewDesiredWidth = WeakHashMap<View, Int>()
var View.desiredWidth: Int?
    get() = ViewDesiredWidth[this]
    set(value) {
        ViewDesiredWidth[this] = value
    }

var dip = 1f
