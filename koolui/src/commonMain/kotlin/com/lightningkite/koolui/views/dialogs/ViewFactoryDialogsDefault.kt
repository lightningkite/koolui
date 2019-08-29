package com.lightningkite.koolui.views.dialogs

import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.layout.DynamicAlignDimensionLayout
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.basic.text
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.interactive.button
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.layout.vertical
import com.lightningkite.koolui.views.root.ViewFactoryRoot

interface ViewFactoryDialogsDefault<VIEW> : ViewFactoryDialogs<VIEW>, ViewFactoryRoot<VIEW>, ViewFactoryLayout<VIEW>, ViewFactoryInteractive<VIEW>, ViewFactoryBasic<VIEW> {

    /**
     * Launches a selector with options to choose from.
     */
    override fun launchSelector(
            title: String?,
            options: List<Pair<String, () -> Unit>>
    ): Unit {
        launchDialog(dismissable = true) { dismiss ->
            scrollVertical(vertical {
                title?.let {
                    -text(text = it, align = AlignPair.CenterCenter, size = TextSize.Subheader)
                }
                for ((text, action) in options) {
                    -button(text, importance = Importance.Low, onClick = { action(); dismiss() })
                }
            })
        }
    }
}