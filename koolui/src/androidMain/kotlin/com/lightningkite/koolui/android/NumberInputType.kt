package com.lightningkite.koolui.android

import android.text.InputType
import com.lightningkite.koolui.concepts.NumberInputType

fun NumberInputType.android(): Int = when (this) {
    NumberInputType.Integer -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
    NumberInputType.Float -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
    NumberInputType.PositiveInteger -> InputType.TYPE_CLASS_NUMBER
    NumberInputType.PositiveFloat -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
}