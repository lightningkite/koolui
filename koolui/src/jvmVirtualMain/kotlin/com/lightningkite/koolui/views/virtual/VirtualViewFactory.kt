@file:Suppress("unused", "CanBeParameter")

package com.lightningkite.koolui.views.virtual

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.lastOrNullObservable
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.recktangle.Point

open class VirtualViewFactory(
        override val theme: Theme = Theme(),
        override val colorSet: ColorSet = theme.main
) : ViewFactory<View> {
    override fun withColorSet(colorSet: ColorSet): VirtualViewFactory = VirtualViewFactory(theme, colorSet)

    override val View.lifecycle: ObservableProperty<Boolean> get() = this.attached

    //DIALOGS
    class LaunchedDialog(var dismissable: kotlin.Boolean, var onDismiss: () -> Unit, var makeView: (dismissDialog: () -> Unit) -> View) : View()
    class LaunchedSelector(var title: String?, var options: List<Pair<String, () -> Unit>>) : View()

    override fun launchDialog(dismissable: Boolean, onDismiss: () -> Unit, makeView: (dismissDialog: () -> Unit) -> View) {}

    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) {}

    override fun contentRoot(view: View): View {
        return super.contentRoot(view).apply {
            attached.alwaysOn = true
        }
    }

    //GENERATED

    override fun button(label: ObservableProperty<String>, imageWithSizing: ObservableProperty<ImageWithSizing?>, importance: Importance, onClick: () -> Unit): ButtonView = ButtonView(label, imageWithSizing, importance, onClick)
    class ButtonView(var label: ObservableProperty<String>, var imageWithSizing: ObservableProperty<ImageWithSizing?>, var importance: Importance, var onClick: () -> Unit) : View()

    override fun card(view: View): CardView = CardView(view)
    class CardView(var view: View) : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): DatePickerView = DatePickerView(observable)
    class DatePickerView(var observable: MutableObservableProperty<Date>) : View()

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): DateTimePickerView = DateTimePickerView(observable)
    class DateTimePickerView(var observable: MutableObservableProperty<DateTime>) : View()

    override fun entryContext(label: String, help: String?, icon: ImageWithSizing?, feedback: ObservableProperty<Pair<Importance, String>?>, field: View): EntryContextView = EntryContextView(label, help, icon, feedback, field)
    class EntryContextView(var label: String, var help: String?, var icon: ImageWithSizing?, var feedback: ObservableProperty<Pair<Importance, String>?>, var field: View)  : ContainerView(){
        override fun listViews(): List<View> = listOf(field)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun align(views: Array<out Pair<AlignPair, View>>): FrameView = FrameView(views)
    class FrameView(var views: Array<out Pair<AlignPair, View>>) : ContainerView(){
        override fun listViews(): List<View> = views.map { it.second }
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun horizontal(views: Array<out Pair<LinearPlacement, View>>): HorizontalView = HorizontalView(views)
    class HorizontalView(var views: Array<out Pair<LinearPlacement, View>>) : ContainerView(){
        override fun listViews(): List<View> = views.map { it.second }
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>): ImageView = ImageView(imageWithSizing)
    class ImageView(var imageWithSizing: ObservableProperty<ImageWithSizing>) : View()

    override fun imageButton(imageWithSizing: ObservableProperty<ImageWithSizing>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit): ImageButtonView = ImageButtonView(imageWithSizing, label, importance, onClick)
    class ImageButtonView(var imageWithSizing: ObservableProperty<ImageWithSizing>, var label: ObservableProperty<String?>, var importance: Importance, var onClick: () -> Unit) : View()

    override fun <T> list(data: ObservableList<T>, firstIndex: MutableObservableProperty<Int>, lastIndex: MutableObservableProperty<Int>, direction: Direction, makeView: (obs: ObservableProperty<T>) -> View): ListView<T> = ListView(data, firstIndex, lastIndex, direction, makeView)
    class ListView<T>(var data: ObservableList<T>, var firstIndex: MutableObservableProperty<Int>, var lastIndex: MutableObservableProperty<Int>, var direction: Direction, var makeView: (obs: ObservableProperty<T>) -> View)  : ContainerView(){
        //TODO: Make this recycle
        var views:List<View> = listOf()
        fun update(){
            views = (firstIndex.value .. lastIndex.value).map { makeView(ConstantObservableProperty(data[it])) }
        }
        init{
            update()
            attached.listen(data.onListUpdate){
                update()
            }
            attached.listen(firstIndex){
                update()
            }
            attached.listen(lastIndex){
                update()
            }
        }
        override fun listViews(): List<View> = views
    }

    override fun numberField(value: MutableObservableProperty<Number?>, placeholder: String, type: NumberInputType, decimalPlaces: kotlin.Int): NumberFieldView = NumberFieldView(value, placeholder, type, decimalPlaces)
    class NumberFieldView(var value: MutableObservableProperty<Number?>, var placeholder: String, var type: NumberInputType, var decimalPlaces: kotlin.Int) : View()

    override fun <DEPENDENCY> pages(dependency: DEPENDENCY, page: MutableObservableProperty<Int>, pageGenerator: Array<out ViewGenerator<DEPENDENCY, View>>): PagesView<DEPENDENCY> = PagesView(dependency, page, pageGenerator)
    class PagesView<DEPENDENCY>(var dependency: DEPENDENCY, var page: MutableObservableProperty<Int>, var pageGenerator: Array<out ViewGenerator<DEPENDENCY, View>>)  : ContainerView(){
        var currentView = pageGenerator[page.value].generate(dependency)
        init{
            currentView.attached.parent = attached
            attached.listen(page){
                currentView.attached.parent = null
                pageGenerator[it].generate(dependency)
                currentView.attached.parent = attached
            }
        }
        override fun listViews(): List<View> = listOf(currentView)
    }

    override fun <T> picker(options: ObservableList<T>, selected: MutableObservableProperty<T>, makeView: (obs: ObservableProperty<T>) -> View): PickerView<T> = PickerView(options, selected, makeView)
    class PickerView<T>(var options: ObservableList<T>, var selected: MutableObservableProperty<T>, var makeView: (obs: ObservableProperty<T>) -> View)  : ContainerView(){
        //TODO: Make this recycle
        var views:List<View> = listOf()
        fun update(){
            views = options.map { makeView(ConstantObservableProperty(it)) }
        }
        init{
            update()
            attached.listen(options.onListUpdate){
                update()
            }
        }
        override fun listViews(): List<View> = views
    }

    override fun progress(view: View, progress: ObservableProperty<Float>): ProgressView = ProgressView(view, progress)
    class ProgressView(var view: View, var progress: ObservableProperty<Float>)  : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun refresh(contains: View, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): RefreshView = RefreshView(contains, working, onRefresh)
    class RefreshView(var contains: View, var working: ObservableProperty<Boolean>, var onRefresh: () -> Unit) : ContainerView(){
        override fun listViews(): List<View> = listOf(contains)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun scrollBoth(view: View, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): ScrollBothView = ScrollBothView(view, amountX, amountY)
    class ScrollBothView(var view: View, var amountX: MutableObservableProperty<Float>, var amountY: MutableObservableProperty<Float>) : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun scrollHorizontal(view: View, amount: MutableObservableProperty<Float>): ScrollHorizontalView = ScrollHorizontalView(view, amount)
    class ScrollHorizontalView(var view: View, var amount: MutableObservableProperty<Float>) : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun scrollVertical(view: View, amount: MutableObservableProperty<Float>): ScrollVerticalView = ScrollVerticalView(view, amount)
    class ScrollVerticalView(var view: View, var amount: MutableObservableProperty<Float>) : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun slider(range: kotlin.ranges.IntRange, observable: MutableObservableProperty<Int>): SliderView = SliderView(range, observable)
    class SliderView(var range: kotlin.ranges.IntRange, var observable: MutableObservableProperty<Int>) : View()

    override fun space(size: Point): SpaceView = SpaceView(size)
    class SpaceView(var size: Point) : View()

    override fun swap(view: ObservableProperty<Pair<View, Animation>>): SwapView = SwapView(view)
    class SwapView(var view: ObservableProperty<Pair<View, Animation>>) : ContainerView(){
        var current = view.value.first
        init{
            current.attached.parent = attached
            attached.bind(view){
                current.attached.parent = null
                current = it.first
                current.attached.parent = attached
            }
        }
        override fun listViews(): List<View> = listOf(current)
    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): TabsView = TabsView(options, selected)
    class TabsView(var options: ObservableList<TabItem>, var selected: MutableObservableProperty<TabItem>) : View()

    override fun text(text: ObservableProperty<String>, importance: Importance, size: TextSize, align: AlignPair, maxLines: Int): TextView = TextView(text, importance, size, align, maxLines)
    class TextView(var text: ObservableProperty<String>, var importance: Importance, var size: TextSize, var align: AlignPair, var maxLines: Int) : View()

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): TextAreaView = TextAreaView(text, placeholder, type)
    class TextAreaView(var text: MutableObservableProperty<String>, var placeholder: String, var type: TextInputType) : View()

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): TextFieldView = TextFieldView(text, placeholder, type)
    class TextFieldView(var text: MutableObservableProperty<String>, var placeholder: String, var type: TextInputType) : View()

    override fun timePicker(observable: MutableObservableProperty<Time>): TimePickerView = TimePickerView(observable)
    class TimePickerView(var observable: MutableObservableProperty<Time>) : View()

    override fun toggle(observable: MutableObservableProperty<Boolean>): ToggleView = ToggleView(observable)
    class ToggleView(var observable: MutableObservableProperty<Boolean>) : View()

    override fun vertical(views: Array<out Pair<LinearPlacement, View>>): VerticalView = VerticalView(views)
    class VerticalView(var views: Array<out Pair<LinearPlacement, View>>) : ContainerView(){
        override fun listViews(): List<View> = views.map { it.second }
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun web(content: ObservableProperty<String>): WebView = WebView(content)
    class WebView(var content: ObservableProperty<String>) : View()

    override fun <DEPENDENCY> window(dependency: DEPENDENCY, stack: MutableObservableList<ViewGenerator<DEPENDENCY, View>>, tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, View>>>): WindowView<DEPENDENCY> = WindowView(dependency, stack, tabs)
    class WindowView<DEPENDENCY>(var dependency: DEPENDENCY, var stack: MutableObservableList<ViewGenerator<DEPENDENCY, View>>, var tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, View>>>): ContainerView(){
        var current = stack.lastOrNull()?.generate(dependency)
        init{
            current?.attached?.parent = attached
            attached.bind(stack.lastOrNullObservable()){
                current?.attached?.parent = null
                current = it?.generate(dependency)
                current?.attached?.parent = attached
            }
        }
        override fun listViews(): List<View> = listOfNotNull(current)
    }

    override fun work(view: View, isWorking: ObservableProperty<Boolean>): WorkView = WorkView(view, isWorking)
    class WorkView(var view: View, var isWorking: ObservableProperty<Boolean>) : ContainerView(){
        override fun listViews(): List<View> = listOf(view)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.alpha(alpha: ObservableProperty<Float>): AlphaView = AlphaView(this, alpha)
    class AlphaView(var receiver: View, var alpha: ObservableProperty<Float>) : ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.altClickable(onAltClick: () -> Unit): AltClickableView = AltClickableView(this, onAltClick)
    class AltClickableView(var receiver: View, var onAltClick: () -> Unit) : ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.background(color: ObservableProperty<Color>): BackgroundView = BackgroundView(this, color)
    class BackgroundView(var receiver: View, var color: ObservableProperty<Color>): ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.clickable(onClick: () -> Unit): ClickableView = ClickableView(this, onClick)
    class ClickableView(var receiver: View, var onClick: () -> Unit) : ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.margin(left: Float, top: Float, right: Float, bottom: Float): MarginView = MarginView(this, left, top, right, bottom)
    class MarginView(var receiver: View, var left: Float, var top: Float, var right: Float, var bottom: Float): ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.setHeight(height: Float): SetHeightView = SetHeightView(this, height)
    class SetHeightView(var receiver: View, var height: Float) : ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }

    override fun View.setWidth(width: Float): SetWidthView = SetWidthView(this, width)
    class SetWidthView(var receiver: View, var width: Float) : ContainerView(){
        override fun listViews(): List<View> = listOf(receiver)
        init{ listViews().forEach { it.attached.parent = attached } }
    }
}
