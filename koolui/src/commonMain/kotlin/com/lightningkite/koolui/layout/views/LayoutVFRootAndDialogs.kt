package com.lightningkite.koolui.layout.views

import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.layout.DynamicAlignDimensionLayout
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.dialogs.ViewFactoryDialogsDefault
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.layout.space
import com.lightningkite.koolui.views.root.ViewFactoryRoot

interface LayoutVFRootAndDialogs<VIEW>
    : ViewFactoryRoot<Layout<*, VIEW>>,
        Themed,
        LayoutViewWrapper<VIEW>,
        ViewFactoryDialogsDefault<Layout<*, VIEW>>,
        ViewFactoryLayout<Layout<*, VIEW>>,
        ViewFactoryBasic<Layout<*, VIEW>> {

    var root: Layout<*, VIEW>?

    override fun contentRoot(view: Layout<*, VIEW>): Layout<*, VIEW> {
        val result = Layout(
                viewAdapter = defaultViewContainer().adapter(),
                x = DynamicAlignDimensionLayout(listOf(Align.Fill to view.x)),
                y = DynamicAlignDimensionLayout(listOf(Align.Fill to view.y))
        )
        result.addChild(view)
        this.root = result
        result.isAttached.alwaysOn = true
        return result
    }

    override fun launchDialog(dismissable: Boolean, onDismiss: () -> Unit, makeView: (dismissDialog: () -> Unit) -> Layout<*, VIEW>) {
        val root = root ?: return
        val x = root.x as? DynamicAlignDimensionLayout ?: return
        val y = root.y as? DynamicAlignDimensionLayout ?: return

        var dismiss: () -> Unit = {}
        val newView = align(
                AlignPair.FillFill to space().background(colorSet.backgroundDisabled.copy(alpha = .5f)).clickable { dismiss() }.margin(0f),
                AlignPair.CenterCenter to makeView { dismiss() }
        )

        x.addChild(Align.Fill to newView.x)
        y.addChild(Align.Fill to newView.y)
        root.addChild(newView)
        applyEntranceTransition(newView.viewAsBase, Animation.Fade)

        dismiss = {
            applyExitTransition(newView.viewAsBase, Animation.Fade) {
                x.removeChild(newView.x)
                y.removeChild(newView.y)
                root.removeChild(newView)
            }
        }
    }
}
