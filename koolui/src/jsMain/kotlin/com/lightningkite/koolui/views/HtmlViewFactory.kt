package com.lightningkite.koolui.views

import com.lightningkite.kommon.asInt8Array
import com.lightningkite.koolui.appendLifecycled
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.builders.*
import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.canvas.HtmlCanvas
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.*
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.implementationhelpers.*
import com.lightningkite.koolui.removeLifecycled
import com.lightningkite.koolui.toWeb
import com.lightningkite.koolui.views.interactive.button
import com.lightningkite.koolui.views.layout.horizontal
import com.lightningkite.koolui.views.layout.vertical
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.*
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import kotlin.browser.document
import kotlin.collections.set
import kotlin.dom.addClass


/**
 * Generates views using HTML.
 * The views generated are unstyled, and CSS is expected to complement them.
 * If you don't have external CSS, you can have it auto inserted using TODO.
 */
class HtmlViewFactory(
        override val theme: Theme,
        override val colorSet: ColorSet = theme.main
) : ViewFactory<HTMLElement> {

    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): HTMLElement {
        return makeElement<HTMLCanvasElement>("canvas") {
            val c = HtmlCanvas(this)
            lifecycle.bind(draw) {
                it(c)
            }
        }
    }

    /*
    LISTING OF CSS SELECTORS THAT WILL BE GENERATED

    .ColorSetMain {}
    .ColorSetBar {}
    .ColorSetAccent {}
    .ColorSetDanger {}
    .ColorSetUnknown {}
    p.TinyText {}
    p.ImportanceLow {}
    p.ImportanceNormal {}
    p.ImportanceHigh {}
    p.ImportanceDanger {}
    h4.ImportanceLow {}
    h4.ImportanceNormal {}
    h4.ImportanceHigh {}
    h4.ImportanceDanger {}
    h1.ImportanceLow {}
    h1.ImportanceNormal {}
    h1.ImportanceHigh {}
    h1.ImportanceDanger {}
    button.ImportanceLow {}
    button.ImportanceNormal {}
    button.ImportanceHigh {}
    button.ImportanceDanger {}
    button.ImportanceLow.ImageFocused {}
    button.ImportanceNormal.ImageFocused {}
    button.ImportanceHigh.ImageFocused {}
    button.ImportanceDanger.ImageFocused {}
    div.card {}
    select {}
    option {}
    input {}
    textarea {}
    img {}
    button {}
    input[type="password"] {}
    input[type="url"] {}
    input[type="email"] {}
    input[type="tel"] {}
    input[type="text"] {}
    input[type="number"] {}
    input[type="date"] {}
    input[type="time"] {}
    input[type="range"] {}
    input[type="checkbox"] {}
    */

    fun defaultCss() = DEFAULT_CSS_TEMPLATE
            .replace("!!mfn", theme.main.foreground.toWeb())
            .replace("!!mfh", theme.main.foregroundHighlighted.toWeb())
            .replace("!!mfd", theme.main.foregroundDisabled.toWeb())
            .replace("!!mbn", theme.main.background.toWeb())
            .replace("!!mbh", theme.main.backgroundHighlighted.toWeb())
            .replace("!!mbd", theme.main.backgroundDisabled.toWeb())
            .replace("!!bfn", theme.bar.foreground.toWeb())
            .replace("!!bfh", theme.bar.foregroundHighlighted.toWeb())
            .replace("!!bfd", theme.bar.foregroundDisabled.toWeb())
            .replace("!!bbn", theme.bar.background.toWeb())
            .replace("!!bbh", theme.bar.backgroundHighlighted.toWeb())
            .replace("!!bbd", theme.bar.backgroundDisabled.toWeb())
            .replace("!!afn", theme.accent.foreground.toWeb())
            .replace("!!afh", theme.accent.foregroundHighlighted.toWeb())
            .replace("!!afd", theme.accent.foregroundDisabled.toWeb())
            .replace("!!abn", theme.accent.background.toWeb())
            .replace("!!abh", theme.accent.backgroundHighlighted.toWeb())
            .replace("!!abd", theme.accent.backgroundDisabled.toWeb())
            .replace("!!dfn", ColorSet.destructive.foreground.toWeb())
            .replace("!!dfh", ColorSet.destructive.foregroundHighlighted.toWeb())
            .replace("!!dfd", ColorSet.destructive.foregroundDisabled.toWeb())
            .replace("!!dbn", ColorSet.destructive.background.toWeb())
            .replace("!!dbh", ColorSet.destructive.backgroundHighlighted.toWeb())
            .replace("!!dbd", ColorSet.destructive.backgroundDisabled.toWeb())

    fun applyDefaultCss() {
        val cssElement = document.createElement("style") as HTMLStyleElement
        cssElement.type = "text/css"
        cssElement.appendChild(document.createTextNode(defaultCss()))
        document.head!!.appendChild(cssElement)
    }

    val colorSetClass = when (colorSet) {
        theme.main -> "ColorSetMain"
        theme.bar -> "ColorSetBar"
        theme.accent -> "ColorSetAccent"
        ColorSet.destructive -> "ColorSetDanger"
        else -> "ColorSetUnknown"
    }

    fun Importance.toCssClass() = when (this) {
        Importance.Low -> "ImportanceLow"
        Importance.Normal -> "ImportanceNormal"
        Importance.High -> "ImportanceHigh"
        Importance.Danger -> "ImportanceDanger"
    }

    override var HTMLElement.lifecycle: TreeObservableProperty
        get() = AnyLifecycles.getOrPut(this) { TreeObservableProperty() }
        set(value) {
            AnyLifecycles[this] = value
        }

    override fun withColorSet(colorSet: ColorSet) = HtmlViewFactory(theme, colorSet)

    fun <T : HTMLElement> makeElement(name: String, setup: T.() -> Unit): T {
        return document.createElement(name).let { it as T }.apply(setup).also { it.addClass(colorSetClass) }
    }

    val mousePosition = Point()
    override fun contentRoot(view: HTMLElement): HTMLElement = makeElement<HTMLDivElement>("div") {
        applyDefaultCss()
        this.id = "root"
        this.style.width = "100%"
        this.style.height = "100%"
        onmousemove = {
            val event = it as MouseEvent
            mousePosition.x = event.clientX.toFloat()
            mousePosition.y = event.clientY.toFloat()
            Unit
        }
        appendLifecycled(view)
        this.lifecycle.alwaysOn = true
    }


    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, HTMLElement>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, HTMLElement>>>
    ): HTMLElement = defaultLargeWindow(theme, withColorSet(theme.bar), dependency, stack, tabs)

    override fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, HTMLElement>
    ): HTMLElement = defaultPages(
            buttonColor = theme.main.foreground,
            dependency = dependency,
            page = page,
            pageGenerator = *pageGenerator
    )

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): HTMLElement {
        return swap(options.onListUpdate.transform {
            horizontal {
                for (option in it) {
                    +button(label = option.text, imageWithSizing = option.imageWithOptions) {
                        selected.value = option
                    }
                }
            } to Animation.Fade
        })
    }

    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
            makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> HTMLElement
    ): HTMLElement = defaultList(
            pageSize = 20,
            buttonColor = colorSet.foreground,
            data = data,
            firstIndex = firstIndex,
            lastIndex = lastIndex,
            direction = direction,
            makeView = makeView
    )

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ): HTMLElement = when (size) {
        TextSize.Tiny -> makeElement<HTMLParagraphElement>("p") {
            addClass("TinyText")
            this.align = when (align.horizontal) {
                Align.Start -> "left"
                Align.Center -> "center"
                Align.End -> "right"
                Align.Fill -> "justify"
            }
            lifecycle.bind(text) {
                this.textContent = it
            }
        }
        TextSize.Body -> makeElement<HTMLParagraphElement>("p") {
            this.align = when (align.horizontal) {
                Align.Start -> "left"
                Align.Center -> "center"
                Align.End -> "right"
                Align.Fill -> "justify"
            }
            lifecycle.bind(text) {
                this.textContent = it
            }
        }
        TextSize.Subheader -> makeElement<HTMLHeadingElement>("h4") {
            this.align = when (align.horizontal) {
                Align.Start -> "left"
                Align.Center -> "center"
                Align.End -> "right"
                Align.Fill -> "justify"
            }
            lifecycle.bind(text) {
                this.textContent = it
            }
        }
        TextSize.Header -> makeElement<HTMLHeadingElement>("h1") {
            this.align = when (align.horizontal) {
                Align.Start -> "left"
                Align.Center -> "center"
                Align.End -> "right"
                Align.Fill -> "justify"
            }
            lifecycle.bind(text) {
                this.textContent = it
            }
        }
    }.apply {
        addClass(importance.toCssClass())
        style.verticalAlign = when (align.vertical) {
            Align.Start -> "top"
            Align.Center -> "middle"
            Align.End -> "bottom"
            Align.Fill -> "middle"
        }
        style.textOverflow = "ellipsis"
        if (maxLines != Int.MAX_VALUE) {
            val em = style.lineHeight.removeSuffix("em").toDoubleOrNull() ?: 1.2
            style.lineHeight = "${em}em"
            style.maxHeight = "${em * maxLines}em"
        }
    }

    override fun image(imageWithOptions: ObservableProperty<ImageWithOptions>): HTMLImageElement = makeElement<HTMLImageElement>("img") {
        lifecycle.bind(imageWithOptions) {
            it.image.url?.let { url ->
                src = url
            } ?: it.image.data?.let { data ->
                val url = URL.createObjectURL(Blob(arrayOf(data.asInt8Array().buffer)))
                src = url
                onload?.invoke(Event("")) //Stops memory leaks when switching images rapidly
                onload = {
                    URL.revokeObjectURL(url)
                }
            }
            style.objectFit = when (it.scaleType) {
                ImageScaleType.Crop -> "cover"
                ImageScaleType.Fill -> "scale-down"
                ImageScaleType.Center -> "none"
            }
            it.defaultSize?.let { pt ->
                style.width = pt.x.toString() + "px"
                style.height = pt.y.toString() + "px"
            }
        }
    }

    override fun web(content: ObservableProperty<String>): HTMLElement = TODO()

    override fun space(size: Point): HTMLElement = makeElement<HTMLDivElement>("div") {
        style.width = size.x.toString() + "px"
        style.height = size.y.toString() + "px"
    }

    override fun button(
            label: ObservableProperty<String>,
            image: ObservableProperty<ImageWithOptions?>,
            importance: Importance,
            onClick: () -> Unit
    ): HTMLButtonElement = makeElement<HTMLButtonElement>("button") {

        addClass(importance.toCssClass())
        type = "button"

        val textNode: HTMLElement =
                with(withColorSet(theme.importance(importance))) { text(label, importance, align = AlignPair.CenterCenter) }
        appendLifecycled(textNode)

        val imageNode: HTMLElement by lazy {
            image(image.transform {
                it ?: MaterialIcon.android.color(Color.white).withOptions()
            })
        }
        var isImageAdded = false
        lifecycle.bind(image) {
            if (it == null) {
                if (isImageAdded) {
                    removeLifecycled(imageNode)
                    isImageAdded = false
                }
            } else {
                if (!isImageAdded) {
                    appendLifecycled(imageNode)
                    isImageAdded = true
                }
            }
        }
        onclick = {
            onClick.invoke()
        }
    }

    override fun imageButton(
            image: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): HTMLButtonElement = makeElement<HTMLButtonElement>("button") {
        addClass(importance.toCssClass(), "ImageFocused")
        type = "button"

        val imageNode: HTMLElement = image(image)
        appendLifecycled(imageNode)

        val textNode: HTMLElement by lazy {
            with(withColorSet(theme.importance(importance))) {
                text(label.transform {
                    it ?: ""
                }, importance, align = AlignPair.CenterCenter)
            }

        }
        var isTextAdded = false
        lifecycle.bind(label) {
            if (it == null) {
                if (isTextAdded) {
                    removeLifecycled(textNode)
                    isTextAdded = false
                }
            } else {
                if (!isTextAdded) {
                    appendLifecycled(textNode)
                    isTextAdded = true
                }
            }
        }
        onclick = {
            onClick.invoke()
        }
    }

    override fun entryContext(
            label: String,
            help: String?,
            icon: ImageWithOptions?,
            feedback: ObservableProperty<Pair<Importance, String>?>,
            field: HTMLElement
    ): HTMLElement = defaultEntryContext(label, help, icon, feedback, field)

    override fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String
    ): HTMLElement = makeElement<HTMLSelectElement>("select") {
        lifecycle.bind(options.onListUpdate) {
            it.forEachIndexed { index, option ->
                this.options.add(document.createElement("option").let { it as HTMLOptionElement }.apply {
                    this.text = toString(option)
                    this.value = index.toString()
                })
            }
            selectedIndex = options.indexOf(selected.value).takeUnless { it == -1 } ?: 0
        }
        this.onselect = { _ ->
            selected.value = options[selectedIndex]
            Unit
        }
        lifecycle.bind(selected) { selectedValue ->
            selectedIndex = options.indexOf(selectedValue).takeUnless { it == -1 } ?: 0
        }
    }

    override fun textField(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ): HTMLElement = makeElement<HTMLInputElement>("input") {
        when (type) {
            TextInputType.Paragraph -> {
                this.type = "text"
            }
            TextInputType.Name -> {
                this.type = "text"; this.autocomplete = "name"
            }
            TextInputType.Password -> {
                this.type = "password"
            }
            TextInputType.Sentence -> {
                this.type = "text"; this.autocomplete = "name"
            }
            TextInputType.CapitalizedIdentifier -> {
                this.type = "text"
            }
            TextInputType.URL -> {
                this.type = "url"
            }
            TextInputType.Email -> {
                this.type = "email"
            }
            TextInputType.Phone -> {
                this.type = "tel"
            }
            TextInputType.Address -> {
                this.type = "text"; this.autocomplete = "street-address"
            }
        }
        this.placeholder = placeholder
        this.oninput = { _ ->
            if (this.value != text.value) {
                text.value = this.value
            }
        }
        lifecycle.bind(text) { textValue ->
            if (this.value != textValue) {
                this.value = textValue
            }
        }
    }

    override fun textArea(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ): HTMLElement = makeElement<HTMLTextAreaElement>("textarea") {
        this.placeholder = placeholder
        this.oninput = { _ ->
            if (this.value != text.value) {
                text.value = this.value
            }
        }
        lifecycle.bind(text) { textValue ->
            if (this.value != textValue) {
                this.value = textValue
            }
        }
    }

    override fun numberField(
            value: MutableObservableProperty<Double?>,
            placeholder: String,
            allowNegatives: Boolean,
            decimalPlaces: Int
    ): HTMLElement = makeElement<HTMLInputElement>("input") {
        this.placeholder = placeholder
        this.type = "number"
        if (!allowNegatives) min = "0"
        step = if (decimalPlaces >= 1) "." + "0".repeat(decimalPlaces - 1) + "1" else "1"
        this.oninput = { _ ->
            if (valueAsNumber != value.value) {
                value.value = valueAsNumber
            }
        }
        lifecycle.bind(value) { value ->
            if (valueAsNumber != value) {
                valueAsNumber = value ?: 0.0
            }
        }
    }

    override fun integerField(
            value: MutableObservableProperty<Long?>,
            placeholder: String,
            allowNegatives: Boolean
    ): HTMLElement = makeElement<HTMLInputElement>("input") {
        this.placeholder = placeholder
        this.type = "number"
        if (!allowNegatives) min = "0"
        step = "1"
        this.oninput = { _ ->
            if (valueAsNumber.toLong() != value.value) {
                value.value = valueAsNumber.toLong()
            }
        }
        lifecycle.bind(value) { value ->
            if (valueAsNumber.toLong() != value) {
                valueAsNumber = value?.toDouble() ?: 0.0
            }
        }
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): HTMLElement =
            makeElement<HTMLInputElement>("input") {
                this.placeholder = placeholder

                fun parse(text: String): Date {
                    return Date.iso8601(text)
                }

                this.type = "date"
                this.oninput = { _ ->
                    val parsed = parse(this.value)
                    if (parsed != observable.value) {
                        observable.value = parsed
                    }
                }
                lifecycle.bind(observable) {
                    val value = observable.value
                    val parsed = parse(this.value)
                    if (parsed != value) {
                        this.value = value.iso8601()
                    }
                }
            }

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): HTMLElement = horizontal(
            LinearPlacement.fillFill to datePicker(observable = observable.transform(
                    mapper = { it.date },
                    reverseMapper = { observable.value.copy(date = it) }
            )),
            LinearPlacement.wrapCenter to space(Point(8f, 8f)),
            LinearPlacement.fillFill to timePicker(observable = observable.transform(
                    mapper = { it.time },
                    reverseMapper = { observable.value.copy(time = it) }
            ))
    )

    override fun timePicker(observable: MutableObservableProperty<Time>): HTMLElement =
            makeElement<HTMLInputElement>("input") {
                this.placeholder = placeholder

                fun parse(text: String): Time {
                    return try {
                        Time(text.substringBefore(':').toInt(), text.substringAfter(':').substringBefore(':').toInt())
                    } catch (e: dynamic) {
                        Time(0)
                    }
                }

                this.type = "time"
                this.oninput = { _ ->
                    val parsed = parse(this.value)
                    if (parsed != observable.value) {
                        observable.value = parsed
                    }
                }
                lifecycle.bind(observable) {
                    val value = observable.value
                    val parsed = parse(this.value)
                    if (parsed != value) {
                        this.value = value.run { "$hours:$minutes:$seconds" }
                    }
                }
            }

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): HTMLElement =
            makeElement<HTMLInputElement>("input") {
                this.placeholder = placeholder

                this.type = "range"
                this.min = range.start.toString()
                this.max = range.endInclusive.toString()
                this.oninput = { _ ->
                    if (this.valueAsNumber.toInt() != observable.value) {
                        observable.value = (this.valueAsNumber.toInt())
                    }
                }
                lifecycle.bind(observable) {
                    val value = observable.value
                    if (this.valueAsNumber.toInt() != value) {
                        this.valueAsNumber = value.toDouble()
                    }
                }
            }

    override fun toggle(observable: MutableObservableProperty<Boolean>): HTMLElement =
            makeElement<HTMLInputElement>("input") {
                this.type = "checkbox"
                this.oninput = { _ ->
                    if (this.checked != observable.value) {
                        observable.value = (this.checked)
                    }
                }
                lifecycle.bind(observable) {
                    val value = observable.value
                    if (this.checked != value) {
                        this.checked = value
                    }
                }
            }

    override fun refresh(
            contains: HTMLElement,
            working: ObservableProperty<Boolean>,
            onRefresh: () -> Unit
    ): HTMLElement = align(
            AlignPair.FillFill to contains,
            AlignPair.TopRight to work(
                    imageButton(
                            imageWithSizing = MaterialIcon.refresh.color(theme.main.foreground).withOptions(
                                    Point(
                                            24f,
                                            24f
                                    )
                            )
                    ) {
                        onRefresh.invoke()
                    }, working
            )
    )

    override fun work(): HTMLElement = image(
            Image.fromSvgString(
                    //<!-- By Sam Herbert (@sherb), for everyone. More @ http://goo.gl/7AJzbL -->
                    """
<svg width="38" height="38" viewBox="0 0 38 38" xmlns="http://www.w3.org/2000/svg" stroke="${colorSet.foreground.toWeb()}">
    <g fill="none" fill-rule="evenodd">
        <g transform="translate(1 1)" stroke-width="2">
            <circle stroke-opacity=".5" cx="18" cy="18" r="18"/>
            <path d="M36 18c0-9.94-8.06-18-18-18">
                <animateTransform
                    attributeName="transform"
                    type="rotate"
                    from="0 18 18"
                    to="360 18 18"
                    dur="1s"
                    repeatCount="indefinite"/>
            </path>
        </g>
    </g>
</svg>"""
            ).withOptions(defaultSize = Point(24f, 24f))
    )

    override fun scrollVertical(view: HTMLElement, amount: MutableObservableProperty<Float>): HTMLElement =
            makeElement("div") {
                appendLifecycled(view.apply {
                    style.maxHeight = ""
                })
                style.overflowY = "auto"

                var suppressListener = false
                lifecycle.bind(amount) {
                    suppressListener = true
                    scrollTop = it.toDouble()
                }
                onscroll = listener@{
                    if (suppressListener) {
                        suppressListener = false
                        return@listener Unit
                    }
                    amount.value = scrollTop.toFloat()
                    Unit
                }
            }

    override fun scrollHorizontal(view: HTMLElement, amount: MutableObservableProperty<Float>): HTMLElement =
            makeElement("div") {
                appendLifecycled(view.apply {
                    style.maxWidth = ""
                })
                style.overflowX = "auto"

                var suppressListener = false
                lifecycle.bind(amount) {
                    suppressListener = true
                    scrollLeft = it.toDouble()
                }
                onscroll = listener@{
                    if (suppressListener) {
                        suppressListener = false
                        return@listener Unit
                    }
                    amount.value = scrollLeft.toFloat()
                    Unit
                }
            }

    override fun scrollBoth(
            view: HTMLElement,
            amountX: MutableObservableProperty<Float>,
            amountY: MutableObservableProperty<Float>
    ): HTMLElement = makeElement("div") {
        appendLifecycled(view.apply {
            style.maxWidth = ""
            style.maxHeight = ""
        })
        style.overflowX = "auto"
        style.overflowY = "auto"

        var suppress = false
        lifecycle.bind(amountX) {
            suppress = true
            scrollLeft = it.toDouble()
        }
        lifecycle.bind(amountY) {
            suppress = true
            scrollTop = it.toDouble()
        }
        onscroll = listener@{
            if (suppress) {
                suppress = false
                return@listener Unit
            }
            amountX.value = scrollLeft.toFloat()
            amountY.value = scrollTop.toFloat()
            Unit
        }
    }

    override fun swap(view: ObservableProperty<Pair<HTMLElement, Animation>>, staticViewForSizing: HTMLElement?): HTMLElement =
            makeElement<HTMLDivElement>("div") {
                style.maxWidth = "100%"
                style.maxHeight = "100%"
                style.position = "relative"

                var currentView: HTMLElement? = null
                lifecycle.bind(view) { (view, animation) ->
                    GlobalScope.launch(Dispatchers.UI) {
                        try {
                            removeLifecycled(currentView!!)
                        } catch (e: dynamic) {/*squish*/
                        }
                        appendLifecycled(view.apply {
                            style.width = "100%"
                            style.height = "100%"
                        })
                        currentView = view
                    }
                }
            }

    override fun horizontal(vararg views: Pair<LinearPlacement, HTMLElement>): HTMLElement =
            makeElement<HTMLDivElement>("div") {
                style.maxWidth = "100%"
                style.maxHeight = "100%"
                style.display = "flex"
                style.flexDirection = "row"
                for ((placement, view) in views) {
                    view.style.alignSelf = placement.align.toWeb()
                    view.style.flexGrow = placement.weight.toString()
                    view.style.flexShrink = placement.weight.toString()
                    if (placement.weight != 0f) {
                        style.width = "100%"
                    }
                    appendLifecycled(view)
                }
            }

    override fun vertical(vararg views: Pair<LinearPlacement, HTMLElement>): HTMLElement =
            makeElement<HTMLDivElement>("div") {
                style.maxWidth = "100%"
                style.maxHeight = "100%"
                style.display = "flex"
                style.flexDirection = "column"
                for ((placement, view) in views) {
                    view.style.alignSelf = placement.align.toWeb()
                    view.style.flexGrow = placement.weight.toString()
                    view.style.flexShrink = placement.weight.toString()
                    if (placement.weight != 0f) {
                        style.height = "100%"
                    }
                    appendLifecycled(view)
                }
            }

    fun measure(element: HTMLElement, out: Point = Point()): Point {
        element.style.position = "static"
        element.style.visibility = "hidden"
        document.body!!.appendChild(element)
        out.x = element.clientWidth.toFloat() + element.clientLeft * 2
        out.y = element.clientHeight.toFloat() + element.clientHeight * 2
        document.body!!.removeChild(element)
        element.style.visibility = "visible"
        element.style.position = "static"
        return out
    }

    override fun align(vararg views: Pair<AlignPair, HTMLElement>): HTMLElement = makeElement<HTMLDivElement>("div") {
        style.maxWidth = "100%"
        style.maxHeight = "100%"
        style.position = "relative"
        val reuse = Point()
        var bestX = 0f
        var bestY = 0f
        for (view in views) {
            measure(view.second, reuse)
            if (reuse.x > bestX) bestX = reuse.x
            if (reuse.y > bestY) bestY = reuse.y
        }
        style.minWidth = bestX.toString() + "px"
        style.minHeight = bestY.toString() + "px"
        for ((align, view) in views) {
            view.style.position = "absolute"
            when (align.horizontal) {
                Align.Start -> view.style.left = "0px"
                Align.Center -> {
                    view.style.left = "50%"
                    view.style.transform = "translateX(-50%)"
                }
                Align.End -> view.style.right = "0px"
                Align.Fill -> view.style.width = "100%"
            }
            when (align.vertical) {
                Align.Start -> view.style.top = "0px"
                Align.Center -> {
                    view.style.top = "50%"
                    view.style.transform += " translateY(-50%)"
                }
                Align.End -> view.style.bottom = "0px"
                Align.Fill -> view.style.height = "100%"
            }
            appendLifecycled(view)
        }
    }

    override fun HTMLElement.setWidth(width: Float): HTMLElement = this.apply {
        style.width = "$width px"
    }

    override fun HTMLElement.setHeight(height: Float): HTMLElement = this.apply {
        style.height = "$height px"
    }

    override fun card(view: HTMLElement): HTMLDivElement = makeElement<HTMLDivElement>("div") {
        classList.add("card")
        appendLifecycled(view)
    }

    override fun HTMLElement.margin(left: Float, top: Float, right: Float, bottom: Float): HTMLElement = this.apply {
        style.marginLeft = "$left px"
        style.marginTop = "$top px"
        style.marginRight = "$right px"
        style.marginBottom = "$bottom px"
    }

    override fun HTMLElement.background(color: ObservableProperty<Color>): HTMLElement = this.apply {
        lifecycle.bind(color) {
            style.backgroundColor = it.toWeb()
        }
    }

    override fun HTMLElement.alpha(alpha: ObservableProperty<Float>): HTMLElement = this.apply {
        lifecycle.bind(alpha) {
            style.opacity = it.toString()
        }
    }

    override fun HTMLElement.clickable(onClick: () -> Unit): HTMLElement = this.apply {
        onclick = {
            it.stopPropagation()
            onClick.invoke()
        }
    }

    override fun HTMLElement.touchable(onNewTouch: (Touch) -> Unit): HTMLElement = this.apply {
        this.onNewTouch(onNewTouch)
    }

    override fun HTMLElement.altClickable(onAltClick: () -> Unit): HTMLElement = this.apply {
        oncontextmenu = {
            it.preventDefault()
            it.stopPropagation()
            onAltClick.invoke()
        }
    }

    override fun launchDialog(
            dismissable: Boolean,
            onDismiss: () -> Unit,
            makeView: (dismissDialog: () -> Unit) -> HTMLElement
    ) {
        document.getElementById("root")?.let { it as HTMLDivElement }?.apply {
            var dialogDismisser = {}
            val newView = align(AlignPair.CenterCenter to makeView { dialogDismisser.invoke() }.clickable { })
                    .background(Color.black.copy(alpha = .5f))
                    .clickable {
                        println("Dialog dismissed")
                        dialogDismisser()
                    }
                    .apply {
                        //position:fixed;top:0;right:0;bottom:0;left:0;z-index:99999999;background-color:rgba(0,0,0,.2);overflow:auto
                        style.position = "fixed"
                        style.top = "0"
                        style.right = "0"
                        style.bottom = "0"
                        style.left = "0"
                        style.overflowWrap = "auto"
                        style.zIndex = "99999999"
                    }
            appendLifecycled(newView)
            var stillActive = true
            dialogDismisser = {
                if (stillActive) {
                    stillActive = false
                    onDismiss()
                    removeLifecycled(newView)
                }
            }
        }
    }

    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) {
        document.getElementById("root")?.let { it as HTMLDivElement }?.apply {
            var dialogDismisser = {}
            val innerView = card(vertical {
                if (title != null) {
                    -text(text = title)
                }
                for (option in options) {
                    -button(label = option.first, importance = Importance.Low) {
                        option.second.invoke()
                    }
                }
            }).apply {
                //                style.width = "128px"
                style.display = "inline-block"
                style.marginLeft = mousePosition.x.toString() + "px"
                style.marginTop = mousePosition.y.toString() + "px"
            }.clickable { }
            val newView = makeElement<HTMLDivElement>("div") {
                style.position = "fixed"
                style.top = "0"
                style.right = "0"
                style.bottom = "0"
                style.left = "0"
                style.overflowWrap = "auto"
                style.zIndex = "99999999"
                appendLifecycled(innerView)
            }
                    .clickable { dialogDismisser() }
            appendLifecycled(newView)
            dialogDismisser = { removeLifecycled(newView) }
        }
    }

    companion object {
        //Replacement:
        //#...\/\*!(...)\*\/
        //
        const val DEFAULT_CSS_TEMPLATE = """
* {
    font-family: Verdana, Geneva, sans-serif;
    margin: 8px;
    overflow: hidden
}
div, html, body {
    margin: 0
}
html, body {
    height: 100vh;
    overflow: hidden;
    background-color: !!mbn
}
.ColorSetMain:active {
    color: !!mfh
}
.ColorSetMain:disabled {
    color: !!mfd
}
.ColorSetMain {
    color: !!mfn
}
.ColorSetBar:active {
    color: !!bfh
}
.ColorSetBar:disabled {
    color: !!bfd
}
.ColorSetBar {
    color: !!bfn
}
.ColorSetAccent:active {
    color: !!afh
}
.ColorSetAccent:disabled {
    color: !!afd
}
.ColorSetAccent {
    color: !!afn
}
.ColorSetDanger:active {
    color: !!dfh
}
.ColorSetDanger:disabled {
    color: !!dfd
}
.ColorSetDanger {
    color: !!dfn
}
p.TinyText {
    font-size: 70%;
}
/*p.ImportanceLow, h4.ImportanceLow, h1.ImportanceLow {
    opacity: .5
}*/
p.ImportanceDanger, h4.ImportanceDanger, h1.ImportanceDanger {
    color: !!dfn
}
button.ImportanceLow {
    color: !!mfn;
    background-color: transparent;
    border-width: 0;
    box-shadow: none;
    transition: background-color .25s
}
button.ImportanceLow:active:hover {
    color: !!mfh;
    background-color: rgba(0,0,0,.2);
    box-shadow: none;
}
button.ImportanceLow:hover {
    color: !!mfh;
    background-color: rgba(0,0,0,.1);
    box-shadow: none;
}
button {
    border-width: 0;
    box-shadow: 0 0 2px rgba(0,0,0,.12),0 2px 2px rgba(0,0,0,.2);
    transition: box-shadow .25s
}
button:active:hover {
    box-shadow: 0 1px 3px rgba(0,0,0,.19),0 6px 6px rgba(0,0,0,.23);
}
button:hover {
    box-shadow: 0 10px 20px rgba(0,0,0,.19),0 6px 6px rgba(0,0,0,.23);
}
button.ImportanceNormal {
    background-color: !!bbn;
}
button.ImportanceHigh {
    background-color: !!abn;
}
button.ImportanceDanger {
    background-color: !!dbn;
}
button.ImportanceLow.ImageFocused {}
button.ImportanceNormal.ImageFocused {}
button.ImportanceHigh.ImageFocused {}
button.ImportanceDanger.ImageFocused {}
div.card {
    background-color: !!mbh;
    margin: 8px;
    padding: 8px;
    box-shadow: 0 1px 3px rgba(0,0,0,.19),0 6px 6px rgba(0,0,0,.23);
}
select {}
option {
    background-color: transparent
}
img {}
textarea {
    overflow: auto
}
textarea, input, select {
    border-width: 0;
    border-color: !!mfd;
    border-bottom-width: 1px;
    background-color: !!mbn;
    transition: border-bottom-width .25s, border-color .25s
}
textarea:hover, input:hover, select:hover, textarea:focus, input:focus, select:focus {
    border-bottom-width: 2px;
    border-color: !!bbn;
}
input[type="password"] {}
input[type="url"] {}
input[type="email"] {}
input[type="tel"] {}
input[type="text"] {}
input[type="number"] {}
input[type="date"] {}
input[type="time"] {}
input[type="range"] {}
input[type="checkbox"] {}
"""
    }
}
