package com.lightningkite.koolui.layout.old

interface DimensionAccess {
    val parent: DimensionCalculator?
    fun updatePlacement(start: Float, end: Float)

    companion object {
        val None = object : DimensionAccess {
            override val parent: DimensionCalculator?
                get() = null

            override fun updatePlacement(start: Float, end: Float) {
            }
        }
    }
}