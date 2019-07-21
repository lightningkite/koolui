package com.lightningkite.koolui.views

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.implementationhelpers.*
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.Event0
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.*
import com.lightningkite.recktangle.Point
import com.lightningkite.recktangle.Rectangle
import kotlinx.coroutines.*

/**
 * PHILOSOPHY
 *
 * The views will function and appear according to the underlying platform.  Styling does
 * not take place at this interface layer.
 *
 * Thus, every function here is meant to represent a concept rather than a specific widget.
 *
 * This interface is meant to be extended and added upon, and only represents the most basic of
 * views needed for creating an app.
 *
 * Layout does take place at this layer, and is meant to be good at resizing.
 *
 * All views are automatically sized unless stated otherwise - either shrinking as small as possible
 * or growing as large as possible.
 *
 * The defaults for spacing are set to look both clean and good - modify them deliberately.
 *
 * The returned view objects are only meant to be used in composing with other views in the factory.
 * Do not attempt to store references to them long-term or anything of the sort.
 */
interface ViewFactory<VIEW> {

    val theme: Theme
    val colorSet: ColorSet

    fun withColorSet(colorSet: ColorSet): ViewFactory<VIEW>

    /**
     * Wraps the given view in another view, if necessary for this view factory to function.
     * Some view factories need a certain root view to be accessible to add dialogs and such.
     */
    fun contentRoot(view: VIEW): VIEW = view

    //Navigation

    /**
     * The main window of the program - provides a stack and tabs, also hosting the actions for the given view generator.
     */
    fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>
    ): VIEW = defaultSmallWindow(theme, withColorSet(theme.bar), dependency, stack, tabs)

    /**
     * A set of ordered pages you can swap through with the built-in navigator.
     */
    fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, VIEW>
    ): VIEW = defaultPages(colorSet.foreground, dependency, page, *pageGenerator)

    /**
     * A set of tabs, with one selected.
     * Does not modify any views outside of it - hook it up to a [swap] for that functionality.
     */
    fun tabs(
            options: ObservableList<TabItem>,
            selected: MutableObservableProperty<TabItem>
    ): VIEW = defaultTabs(options, selected)


    //Collection Views

    /**
     * Shows a list of items and notifies you when it's scrolled to the end.
     */
    fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            lastIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            direction: Direction = Direction.Down,
            makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> VIEW
    ): VIEW = defaultList(10, colorSet.foreground, data, direction, firstIndex, lastIndex, makeView)

    /**
     * Shows a list of items and notifies you when it's scrolled to the end.
     */
    fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            lastIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            direction: Direction = Direction.Down,
            makeView: (obs: ObservableProperty<T>) -> VIEW
    ): VIEW = list(data, firstIndex, lastIndex, direction) { obs, index -> makeView(obs) }


    //Display

    /**
     * Shows a piece of text at the given size.
     */
    fun text(
            text: ObservableProperty<String>,
            importance: Importance = Importance.Normal,
            size: TextSize = TextSize.Body,
            align: AlignPair = AlignPair.CenterLeft,
            maxLines: Int = Int.MAX_VALUE
    ): VIEW

    /**
     * Shows an image with the given scaling.
     * While loading, it will show a loading indicator.
     */
    fun image(
            imageWithSizing: ObservableProperty<ImageWithSizing>
    ): VIEW

    /**
     * Shows a webpage within this view.
     * If the string starts with 'http://', it will be interpreted as a URL.
     * Otherwise, it will be interpreted as non-interactive HTML content.
     */
    fun web(
            content: ObservableProperty<String>
    ): VIEW

    /**
     * Creates a blank space.
     */
    fun space(size: Point): VIEW

    /**
     * A working indicator.
     */
    fun work(): VIEW = text(ConstantObservableProperty("Working..."))

    /**
     * Shows the given view if not working, otherwise shows a progress indicator.
     * @param progress A number 0-1 showing the amount of progress made on a task.  When the value is 1,
     */
    fun progress(progress: ObservableProperty<Float>): VIEW = text(progress.transform { it.times(100).toInt().toString() + "%" })


    //Activation Controls

    /**
     * A button with the given image and label.  Runs [onClick] when the button is interacted with.
     */
    fun button(
            label: ObservableProperty<String>,
            imageWithSizing: ObservableProperty<ImageWithSizing?> = ConstantObservableProperty(null),
            importance: Importance = Importance.Normal,
            onClick: () -> Unit
    ): VIEW = withColorSet(theme.importance(importance)).run {
        text(label).background(colorSet.background).clickable(onClick)
    }

    /**
     * A button with the given image and label.  Runs [onClick] when the button is interacted with.
     * Attempts to use image over text.
     */
    fun imageButton(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?> = ConstantObservableProperty(null),
            importance: Importance = Importance.Normal,
            onClick: () -> Unit
    ): VIEW = withColorSet(theme.importance(importance)).run {
        image(imageWithSizing).background(colorSet.background).clickable(onClick)
    }


    //Entry Controls

    /**
     * Perhaps the most complex part - a wrapper around controls that gives them context.
     * @param label A label for the given field.
     * @param help A helpful description of what is to be entered here.  Showed only through interaction.
     * @param icon An icon that represents the field.
     * @param feedback Feedback for the user.
     * @param field The field itself.
     */
    fun entryContext(
            label: String,
            help: String? = null,
            icon: ImageWithSizing? = null,
            feedback: ObservableProperty<Pair<Importance, String>?> = ConstantObservableProperty(null),
            field: VIEW
    ): VIEW = defaultEntryContext(label, help, icon, feedback, field)

    /**
     * An element that allows the user to select an item from a list.
     */
    fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String = { it.toString() }
    ): VIEW

    /**
     * An element that allows the user to enter a small piece of text.
     */
    fun textField(
            text: MutableObservableProperty<String>,
            placeholder: String = "",
            type: TextInputType = TextInputType.Name
    ): VIEW

    /**
     * An element that allows the user to enter a large piece of text.
     */
    fun textArea(
            text: MutableObservableProperty<String>,
            placeholder: String = "",
            type: TextInputType = TextInputType.Paragraph
    ): VIEW

    /**
     * An element that allows the user to enter a number.
     */
    fun integerField(
            value: MutableObservableProperty<Long?>,
            placeholder: String = "",
            allowNegatives: Boolean = false
    ): VIEW = textField(
            text = value.transform(
                    mapper = { it?.toString() ?: "" },
                    reverseMapper = { it.toLongOrNull() }
            )
    )

    /**
     * An element that allows the user to enter a number.
     */
    fun numberField(
            value: MutableObservableProperty<Double?>,
            placeholder: String = "",
            allowNegatives: Boolean = false,
            decimalPlaces: Int = 0
    ): VIEW = textField(
            text = value.transform(
                    mapper = { it?.toString() ?: "" },
                    reverseMapper = { it.toDoubleOrNull() }
            )
    )

    /**
     * An element that allows the user to enter a date.
     */
    fun datePicker(
            observable: MutableObservableProperty<Date>
    ): VIEW = defaultDatePicker(observable)

    /**
     * An element that allows the user to enter a date and time.
     */
    fun dateTimePicker(
            observable: MutableObservableProperty<DateTime>
    ): VIEW = horizontal(
            LinearPlacement.wrapFill to datePicker(observable.transform(
                    mapper = { it.date },
                    reverseMapper = { observable.value.copy(date = it) }
            )),
            LinearPlacement.wrapFill to timePicker(observable.transform(
                    mapper = { it.time },
                    reverseMapper = { observable.value.copy(time = it) }
            ))
    )

    /**
     * An element that allows the user to enter a time.
     */
    fun timePicker(
            observable: MutableObservableProperty<Time>
    ): VIEW = defaultTimePicker(observable)

    /**
     * A slider that lets the user pick a value within the given range.
     */
    fun slider(
            range: IntRange,
            observable: MutableObservableProperty<Int>
    ): VIEW

    /**
     * A switch or checkbox (depending on platform) that the user can turn on or off.
     */
    fun toggle(
            observable: MutableObservableProperty<Boolean>
    ): VIEW


    //Containers

    /**
     * Wraps content with a control to refresh the data contained within.
     * On mobile, enables a "swipe to refresh" gesture.
     */
    fun refresh(
            contains: VIEW,
            working: ObservableProperty<Boolean>,
            onRefresh: () -> Unit
    ): VIEW = defaultRefresh(contains, working, onRefresh)

    /**
     * Wraps content to make it scroll vertically.
     */
    fun scrollVertical(
            view: VIEW,
            amount: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW = scrollBoth(view, amountY = amount)

    /**
     * Wraps content to make it scroll horizontally.
     */
    fun scrollHorizontal(
            view: VIEW,
            amount: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW = scrollBoth(view, amountX = amount)

    /**
     * Wraps content to make it scroll both directions.
     */
    fun scrollBoth(
            view: VIEW,
            amountX: MutableObservableProperty<Float> = StandardObservableProperty(0f),
            amountY: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW

    /**
     * Shows a single view at a time, which can be switched out with another through animation.
     * If you provide a [staticViewForSizing], the view's size will always be based on that view instead of another.
     */
    fun swap(
            view: ObservableProperty<Pair<VIEW, Animation>>,
            staticViewForSizing: VIEW? = null
    ): VIEW

    /**
     * Places elements horizontally, left to right.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun horizontal(
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW

    /**
     * Places elements vertically, top to bottom.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun vertical(
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW

    /**
     * Places elements linearly, either left to right or top to bottom.
     * The orientation is determined automatically based on what the system decides fits the elements best.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun linear(
            defaultToHorizontal: Boolean = false,
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW = if (defaultToHorizontal) horizontal(*views) else vertical(*views)

    /**
     * Places elements on top of each other, back to front.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun align(
            vararg views: Pair<AlignPair, VIEW>
    ): VIEW

    /**
     * Frames these elements separately.
     */
    fun frame(
            view: VIEW
    ): VIEW = align(AlignPair.FillFill to view)

    /**
     * Adds a 'card' background to the given item.
     */
    fun card(view: VIEW): VIEW = frame(view).margin(8f).background(colorSet.backgroundHighlighted)


    //Modifiers

    /**
     * Adds a margin around an item.
     */
    fun VIEW.margin(
            left: Float = 0f,
            top: Float = 0f,
            right: Float = 0f,
            bottom: Float = 0f
    ): VIEW

    fun VIEW.margin(
            horizontal: Float = 0f,
            top: Float = 0f,
            bottom: Float = 0f
    ) = this.margin(horizontal, top, horizontal, bottom)

    fun VIEW.margin(
            horizontal: Float = 0f,
            vertical: Float = 0f
    ) = this.margin(horizontal, vertical, horizontal, vertical)

    fun VIEW.margin(amount: Float) = this.margin(amount, amount, amount, amount)

    /**
     * Adds a background to the given item.
     */
    fun VIEW.background(
            color: ObservableProperty<Color>
    ): VIEW

    fun VIEW.background(
            color: Color
    ): VIEW = background(ConstantObservableProperty(color))

    /**
     * Changes the alpha of a view.
     */
    fun VIEW.alpha(
            alpha: ObservableProperty<Float>
    ): VIEW

    fun VIEW.alpha(
            alpha: Float
    ): VIEW = alpha(ConstantObservableProperty(alpha))

    /**
     * Makes a normally non-clickable element clickable.
     */
    fun VIEW.clickable(
            onClick: () -> Unit
    ): VIEW

    /**
     * Adds an alternate click to the element.
     * On a desktop, this manifests as right-click.
     * On a mobile device, this manifests as a long click.
     * On web it... uh... I dunno.
     */
    fun VIEW.altClickable(
            onAltClick: () -> Unit
    ): VIEW

    /**
     * Forces the view to be of a certain size
     */
    fun VIEW.setWidth(width: Float): VIEW

    /**
     * Forces the view to be of a certain size
     */
    fun VIEW.setHeight(height: Float): VIEW

    /**
     * Gets the lifecycle of a view.
     */
    val VIEW.lifecycle: ObservableProperty<Boolean>

    /**
     * Gets the coroutine scope of a view.
     */
    val VIEW.scope: CoroutineScope
        get() {
            val scope = CoroutineScope(Dispatchers.UI)
            GlobalScope.launch(Dispatchers.UI) {
                var willHoldLambda: (Boolean) -> Unit = {}
                willHoldLambda = {
                    if (!it) {
                        scope.cancel()
                        GlobalScope.launch(Dispatchers.UI) {
                            lifecycle.remove(willHoldLambda)
                        }
                    }
                }
                lifecycle.add(willHoldLambda)
            }
            return scope
        }


    //Dialogs and Selectors

    /**
     * Launches a dialog with the given view in it.
     */
    fun launchDialog(
            dismissable: Boolean = true,
            onDismiss: () -> Unit = {},
            makeView: (dismissDialog: () -> Unit) -> VIEW
    ): Unit

    /**
     * Launches a selector with options to choose from.
     */
    fun launchSelector(
            title: String? = null,
            options: List<Pair<String, () -> Unit>>
    ): Unit = defaultLaunchSelector(title, options)


    fun canvas(
            updateEvent: Event0,
            draw: Canvas.()->Unit
    ): VIEW
}

interface Canvas {
    val size: Point

    val matrix: Matrix

    fun move(x: Float, y: Float)
    fun line(x: Float, y: Float)
    fun curve(controlX: Float, controlY: Float, x: Float, y:Float)
    fun setPaint(paint: Paint)
    fun clearRect(rectangle: Rectangle)
    fun drawImage(image: Image)
}

data class Paint(
        val fill: Color,
        val stroke: Color,
        val strokeSize: Float
)