package com.lightningkite.koolui.views

import com.lightningkite.koolui.concepts.Importance


fun Importance.toCssClass() = when (this) {
    Importance.Low -> "ImportanceLow"
    Importance.Normal -> "ImportanceNormal"
    Importance.High -> "ImportanceHigh"
    Importance.Danger -> "ImportanceDanger"
}