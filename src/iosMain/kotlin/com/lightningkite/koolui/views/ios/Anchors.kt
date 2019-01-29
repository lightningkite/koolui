package com.lightningkite.koolui.views.ios

import platform.UIKit.*

interface Anchors {
    val bottomAnchor: NSLayoutYAxisAnchor
    val centerXAnchor: NSLayoutXAxisAnchor
    val centerYAnchor: NSLayoutYAxisAnchor
    val heightAnchor: NSLayoutDimension
    val leadingAnchor: NSLayoutXAxisAnchor
    val leftAnchor: NSLayoutXAxisAnchor
    val rightAnchor: NSLayoutXAxisAnchor
    val topAnchor: NSLayoutYAxisAnchor
    val trailingAnchor: NSLayoutXAxisAnchor
    val widthAnchor: NSLayoutDimension
}

fun UIView.anchors(): Anchors = object : Anchors {
    override val bottomAnchor get() = this@anchors.bottomAnchor
    override val centerXAnchor get() = this@anchors.centerXAnchor
    override val centerYAnchor get() = this@anchors.centerYAnchor
    override val heightAnchor get() = this@anchors.heightAnchor
    override val leadingAnchor get() = this@anchors.leadingAnchor
    override val leftAnchor get() = this@anchors.leftAnchor
    override val rightAnchor get() = this@anchors.rightAnchor
    override val topAnchor get() = this@anchors.topAnchor
    override val trailingAnchor get() = this@anchors.trailingAnchor
    override val widthAnchor get() = this@anchors.widthAnchor
}

fun UILayoutGuide.anchors(): Anchors = object : Anchors {
    override val bottomAnchor get() = this@anchors.bottomAnchor
    override val centerXAnchor get() = this@anchors.centerXAnchor
    override val centerYAnchor get() = this@anchors.centerYAnchor
    override val heightAnchor get() = this@anchors.heightAnchor
    override val leadingAnchor get() = this@anchors.leadingAnchor
    override val leftAnchor get() = this@anchors.leftAnchor
    override val rightAnchor get() = this@anchors.rightAnchor
    override val topAnchor get() = this@anchors.topAnchor
    override val trailingAnchor get() = this@anchors.trailingAnchor
    override val widthAnchor get() = this@anchors.widthAnchor
}
