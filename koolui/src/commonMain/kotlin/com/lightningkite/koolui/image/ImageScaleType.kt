package com.lightningkite.koolui.image

/**
 * Describes how an image should scale.
 */
enum class ImageScaleType {
    /**
     * Make the image fill the whole space, cropping the edges to fit.
     */
    Crop,
    /**
     * Make the image fill the space, but makes sure the ratio stays correct.
     */
    Fill,
    /**
     * The image will take as much space as it takes, and will simply be centered.
     */
    Center
}
