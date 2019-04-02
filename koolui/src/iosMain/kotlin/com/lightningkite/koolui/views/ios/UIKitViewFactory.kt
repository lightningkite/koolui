package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.implementationhelpers.defaultList
import com.lightningkite.koolui.implementationhelpers.defaultPages
import com.lightningkite.koolui.implementationhelpers.defaultSmallWindow
import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*
import platform.objc.objc_setAssociatedObject

class UIKitViewFactory(override val theme: Theme, override val colorSet: ColorSet) : ViewFactory<Layout<*, UIView>> {

    override fun withColorSet(colorSet: ColorSet): ViewFactory<Layout<*, UIView>> = UIKitViewFactory(theme, colorSet)

    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, Layout<*, UIView>>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, Layout<*, UIView>>>>
    ): Layout<*, UIView> = defaultSmallWindow(theme, withColorSet(theme.bar), dependency, stack, tabs)

    override fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, Layout<*, UIView>>
    ): Layout<*, UIView> = defaultPages(theme.bar.background, dependency, page, *pageGenerator)

    override fun horizontal(vararg views: Pair<LinearPlacement, Layout<*, UIView>>): Layout<UIView, UIView> = Layout.horizontal(
            viewAdapter = UIView(frame = CGRect.zeroVal).adapter,
            children = views.toList()
    ).apply {
        views.forEach {
            viewAdapter.view.addSubview(it.second.viewAdapter.viewAsBase)
        }
    }

    override fun vertical(vararg views: Pair<LinearPlacement, Layout<*, UIView>>): Layout<UIView, UIView> = Layout.vertical(
            viewAdapter = UIView(frame = CGRect.zeroVal).adapter,
            children = views.toList()
    ).apply {
        views.forEach {
            viewAdapter.view.addSubview(it.second.viewAdapter.viewAsBase)
        }
    }

    override fun align(vararg views: Pair<AlignPair, Layout<*, UIView>>): Layout<UIView, UIView> = Layout.align(
            viewAdapter = UIView(frame = CGRect.zeroVal).adapter,
            children = views.toList()
    ).apply {
        views.forEach {
            viewAdapter.view.addSubview(it.second.viewAdapter.viewAsBase)
        }
    }

    override fun frame(view: Layout<*, UIView>): Layout<*, UIView> = Layout.frame(
            viewAdapter = UIView(frame = CGRect.zeroVal).adapter,
            child = view
    )

    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
            makeView: (obs: ObservableProperty<T>) -> Layout<*, UIView>
    ): Layout<*, UIView> = defaultList(100, theme.bar.background, data, direction, firstIndex, lastIndex, makeView)

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ) = Layout.intrinsic(UILabel(frame = CGRect.zeroVal)).apply {
        lifecycle.bind(text){
            viewAdapter.view.text = it
        }
        viewAdapter.view.textColor = colorSet.importance(importance).ios
        viewAdapter.view.font = UIFont.systemFontOfSize(size.ios)
        viewAdapter.view.textAlignment = when(align.horizontal) {
            Align.Start -> NSTextAlignmentLeft
            Align.Center -> NSTextAlignmentCenter
            Align.End -> NSTextAlignmentRight
            Align.Fill -> NSTextAlignmentJustified
        }
        viewAdapter.view.numberOfLines = maxLines.toLong()
        //TODO: Vertical align?
    }

    override fun button(
            label: ObservableProperty<String>,
            imageWithSizing: ObservableProperty<ImageWithSizing?>,
            importance: Importance,
            onClick: () -> Unit
    ) = Layout.intrinsic(UIButton(frame = CGRect.zeroVal)).apply {
        lifecycle.bind(label){
            viewAdapter.view.setTitle(it, UIControlStateNormal)
        }
        viewAdapter.view.setTitleColor(colorSet.importance(importance).ios, UIControlStateNormal)
        viewAdapter.addAction(UIControlEventTouchUpInside, onClick)
        //TODO: Image
    }




    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): Layout<*, UIView> = text(text = "tabs")

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>): Layout<*, UIView> = text(text = "image")

    override fun web(content: ObservableProperty<String>): Layout<*, UIView> = text(text = "web")

    override fun space(size: Point): Layout<*, UIView> = text(text = "space").setHeight(size.y).setWidth(size.x)

    override fun imageButton(imageWithSizing: ObservableProperty<ImageWithSizing>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit): Layout<*, UIView> = text(text = "imageButton")

    override fun entryContext(label: String, help: String?, icon: ImageWithSizing?, feedback: ObservableProperty<Pair<Importance, String>?>, field: Layout<*, UIView>): Layout<*, UIView> = text(text = "entryContext")

    override fun <T> picker(options: ObservableList<T>, selected: MutableObservableProperty<T>, makeView: (obs: ObservableProperty<T>) -> Layout<*, UIView>): Layout<*, UIView> = text(text = "<")

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, UIView> = text(text = "textField")

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, UIView> = text(text = "textArea")

    override fun numberField(value: MutableObservableProperty<Number?>, placeholder: String, type: NumberInputType, decimalPlaces: Int): Layout<*, UIView> = text(text = "numberField")

    override fun datePicker(observable: MutableObservableProperty<Date>): Layout<*, UIView> = text(text = "datePicker")

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): Layout<*, UIView> = text(text = "dateTimePicker")

    override fun timePicker(observable: MutableObservableProperty<Time>): Layout<*, UIView> = text(text = "timePicker")

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): Layout<*, UIView> = text(text = "slider")

    override fun toggle(observable: MutableObservableProperty<Boolean>): Layout<*, UIView> = text(text = "toggle")

    override fun scrollBoth(view: Layout<*, UIView>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(contains: Layout<*, UIView>, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun work(view: Layout<*, UIView>, isWorking: ObservableProperty<Boolean>): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun progress(view: Layout<*, UIView>, progress: ObservableProperty<Float>): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun swap(view: ObservableProperty<Pair<Layout<*, UIView>, Animation>>): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun card(view: Layout<*, UIView>): Layout<*, UIView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Layout<*, UIView>.margin(
            left: Float,
            top: Float,
            right: Float,
            bottom: Float
    ): Layout<*, UIView> = Layout.frame(
            viewAdapter = UIView(CGRect.zeroVal).adapter,
            child = this,
            leftMargin = left,
            rightMargin = right,
            topMargin = top,
            bottomMargin = bottom
    )

    override fun Layout<*, UIView>.background(color: ObservableProperty<Color>): Layout<*, UIView> {
        lifecycle.bind(color){
            viewAdapter.viewAsBase.setBackgroundColor(it.ios)
        }
        return this
    }

    override fun Layout<*, UIView>.alpha(alpha: ObservableProperty<Float>): Layout<*, UIView> {
        lifecycle.bind(alpha){
            viewAdapter.viewAsBase.setAlpha(it.toDouble())
        }
        return this
    }

    override fun Layout<*, UIView>.clickable(onClick: () -> Unit): Layout<*, UIView> {
        viewAdapter.addGestureRecognizer(UITapGestureRecognizer(), onClick)
    }

    override fun Layout<*, UIView>.altClickable(onAltClick: () -> Unit): Layout<*, UIView> {
        viewAdapter.addGestureRecognizer(UILongPressGestureRecognizer(), onAltClick)
    }

    override fun Layout<*, UIView>.setWidth(width: Float): Layout<*, UIView> {
        (this as Layout<UIView, UIView>).forceWidth(width)
        return this
    }

    override fun Layout<*, UIView>.setHeight(height: Float): Layout<*, UIView> {
        (this as Layout<UIView, UIView>).forceHeight(height)
        return this
    }

    override val Layout<*, UIView>.lifecycle: ObservableProperty<Boolean> get() = this.lifecycle

    override fun launchDialog(dismissable: Boolean, onDismiss: () -> Unit, makeView: (dismissDialog: () -> Unit) -> Layout<*, UIView>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


