package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point
import platform.UIKit.*
import kotlin.math.max

data class SubLayout(
        val views: ArrayList<UIView>,
        val guides: ArrayList<UILayoutGuide>,
        var anchors: Anchors,
        var leftMargin: Double = 0.0,
        var rightMargin: Double = 0.0,
        var topMargin: Double = 0.0,
        var bottomMargin: Double = 0.0
) {
    constructor(view: UIView) : this(
            views = arrayListOf(view),
            guides = ArrayList(),
            anchors = view.anchors()
    )

    constructor(guide: UILayoutGuide) : this(
            views = arrayListOf(),
            guides = arrayListOf(guide),
            anchors = guide.anchors()
    )
}

class UIKitViewFactory(override val theme: Theme, override val colorSet: ColorSet) : ViewFactory<SubLayout> {
    override fun withColorSet(colorSet: ColorSet): ViewFactory<SubLayout> = UIKitViewFactory(theme, colorSet)

    fun UIView.applyDefaults() {
        setContentHuggingPriority(500f, UILayoutConstraintAxisHorizontal)
        setContentHuggingPriority(500f, UILayoutConstraintAxisVertical)
    }

    fun NSLayoutConstraint.applyDefaults() {
        priority = 250f
    }

    fun NSLayoutConstraint.strengthen() {
        priority += 1f
    }

    fun NSLayoutConstraint.absolute() {
        priority = 1000f
    }

    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, SubLayout>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, SubLayout>>>
    ): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, SubLayout>
    ): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
            makeView: (obs: ObservableProperty<T>) -> SubLayout
    ): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun text(text: ObservableProperty<String>, importance: Importance, size: TextSize, align: AlignPair, maxLines: Int): SubLayout {
        return SubLayout(UILabel().apply {
            this.text = text.value
            this.numberOfLines = Long.MAX_VALUE
            this.textColor = when (importance) {
                Importance.Low -> colorSet.foregroundDisabled
                Importance.Normal -> colorSet.foreground
                Importance.High -> colorSet.foregroundHighlighted
                Importance.Danger -> ColorSet.destructive.foreground
            }.ios
            this.font = this.font.fontWithSize(when (size) {
                TextSize.Tiny -> 10.0
                TextSize.Body -> 17.0
                TextSize.Subheader -> 25.0
                TextSize.Header -> 34.0
            })
            this.textAlignment = when (align) {
                AlignPair.TopLeft -> NSTextAlignmentLeft
                AlignPair.TopCenter -> NSTextAlignmentCenter
                AlignPair.TopFill -> NSTextAlignmentJustified
                AlignPair.TopRight -> NSTextAlignmentRight
                AlignPair.CenterLeft -> NSTextAlignmentLeft
                AlignPair.CenterCenter -> NSTextAlignmentCenter
                AlignPair.CenterFill -> NSTextAlignmentJustified
                AlignPair.CenterRight -> NSTextAlignmentRight
                AlignPair.FillLeft -> NSTextAlignmentLeft
                AlignPair.FillCenter -> NSTextAlignmentCenter
                AlignPair.FillFill -> NSTextAlignmentJustified
                AlignPair.FillRight -> NSTextAlignmentRight
                AlignPair.BottomLeft -> NSTextAlignmentLeft
                AlignPair.BottomCenter -> NSTextAlignmentCenter
                AlignPair.BottomFill -> NSTextAlignmentJustified
                AlignPair.BottomRight -> NSTextAlignmentRight
            }
            //TODO: No vertical alignment
        })
    }

    override fun image(image: ObservableProperty<Image>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun web(content: ObservableProperty<String>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun space(size: Point): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun button(label: ObservableProperty<String>, image: ObservableProperty<Image?>, importance: Importance, onClick: () -> Unit): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun imageButton(image: ObservableProperty<Image>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun entryContext(label: String, help: String?, icon: Image?, feedback: ObservableProperty<Pair<Importance, String>?>, field: SubLayout): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> picker(options: ObservableList<T>, selected: MutableObservableProperty<T>, makeView: (obs: ObservableProperty<T>) -> SubLayout): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun numberField(value: MutableObservableProperty<Number?>, placeholder: String, type: NumberInputType, decimalPlaces: Int): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun timePicker(observable: MutableObservableProperty<Time>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(contains: SubLayout, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun work(view: SubLayout, isWorking: ObservableProperty<Boolean>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun progress(view: SubLayout, progress: ObservableProperty<Float>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scrollBoth(view: SubLayout, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun swap(view: ObservableProperty<Pair<SubLayout, Animation>>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun horizontal(vararg views: Pair<LinearPlacement, SubLayout>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun vertical(vararg views: Pair<LinearPlacement, SubLayout>): SubLayout {
        val guide = UILayoutGuide()
        val layout = SubLayout(guide)

        val leftMargin = views.asSequence().map { it.second.leftMargin }.min() ?: 0.0
        val rightMargin = views.asSequence().map { it.second.rightMargin }.min() ?: 0.0
        val topMargin = views.firstOrNull()?.second?.topMargin ?: 0.0
        val bottomMargin = views.lastOrNull()?.second?.bottomMargin ?: 0.0

        var expandingView: SubLayout? = null
        var previousView: SubLayout? = null

        views.forEachIndexed { index, (placement, subview) ->

            layout.views.addAll(subview.views)
            layout.guides.addAll(subview.guides)

            if (previousView == null) {
                subview.anchors.topAnchor.constraintEqualToAnchor(layout.anchors.topAnchor)
            } else {
                val margin = max(previousView!!.bottomMargin, subview.topMargin)
                subview.anchors.topAnchor.constraintEqualToAnchor(previousView!!.anchors.bottomAnchor, margin)
            }

            subview.anchors.leftAnchor.constraintEqualToAnchor(layout.anchors.leftAnchor, subview.leftMargin - leftMargin)
            subview.anchors.rightAnchor.constraintEqualToAnchor(layout.anchors.rightAnchor, subview.rightMargin - rightMargin)

            previousView = subview
        }

        layout.leftMargin = leftMargin
        layout.rightMargin = rightMargin
        layout.topMargin = topMargin
        layout.bottomMargin = bottomMargin
        return layout
    }

    override fun frame(vararg views: Pair<AlignPair, SubLayout>): SubLayout {
        val guide = UILayoutGuide()
        val layout = SubLayout(guide)
        for ((align, sub) in views) {
            when (align.horizontal) {
                Align.Start -> {
                    sub.anchors.leadingAnchor.constraintEqualToAnchor(layout.anchors.leadingAnchor, sub.leftMargin)
                    sub.anchors.trailingAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.trailingAnchor, sub.rightMargin)
                }
                Align.Center -> {
                    sub.anchors.leadingAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.leadingAnchor, sub.leftMargin)
                    sub.anchors.trailingAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.trailingAnchor, sub.rightMargin)
                    sub.anchors.centerXAnchor.constraintEqualToAnchor(layout.anchors.centerXAnchor)
                }
                Align.End -> {
                    sub.anchors.leadingAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.leadingAnchor, sub.leftMargin)
                    sub.anchors.trailingAnchor.constraintEqualToAnchor(layout.anchors.trailingAnchor, sub.rightMargin)
                }
                Align.Fill -> {
                    sub.anchors.leadingAnchor.constraintEqualToAnchor(layout.anchors.leadingAnchor, sub.leftMargin)
                    sub.anchors.trailingAnchor.constraintEqualToAnchor(layout.anchors.trailingAnchor, sub.rightMargin)
                }
            }
            when (align.vertical) {
                Align.Start -> {
                    sub.anchors.topAnchor.constraintEqualToAnchor(layout.anchors.topAnchor, sub.topMargin)
                    sub.anchors.bottomAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.bottomAnchor, sub.bottomMargin)
                }
                Align.Center -> {
                    sub.anchors.topAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.topAnchor, sub.topMargin)
                    sub.anchors.bottomAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.bottomAnchor, sub.bottomMargin)
                    sub.anchors.centerYAnchor.constraintEqualToAnchor(layout.anchors.centerYAnchor)
                }
                Align.End -> {
                    sub.anchors.topAnchor.constraintLessThanOrEqualToAnchor(layout.anchors.topAnchor, sub.topMargin)
                    sub.anchors.bottomAnchor.constraintEqualToAnchor(layout.anchors.bottomAnchor, sub.bottomMargin)
                }
                Align.Fill -> {
                    sub.anchors.topAnchor.constraintEqualToAnchor(layout.anchors.topAnchor, sub.topMargin)
                    sub.anchors.bottomAnchor.constraintEqualToAnchor(layout.anchors.bottomAnchor, sub.bottomMargin)
                }
            }
            layout.views.addAll(sub.views)
            layout.guides.addAll(sub.guides)
        }
        return layout
    }

    override fun card(view: SubLayout): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.margin(left: Float, top: Float, right: Float, bottom: Float): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.background(color: ObservableProperty<Color>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.alpha(alpha: ObservableProperty<Float>): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.clickable(onClick: () -> Unit): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.altClickable(onAltClick: () -> Unit): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.setWidth(width: Float): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SubLayout.setHeight(height: Float): SubLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchDialog(dismissable: Boolean, onDismiss: () -> Unit, makeView: (dismissDialog: () -> Unit) -> SubLayout) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val SubLayout.lifecycle: ObservableProperty<Boolean>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}
