package com.lightningkite.koolui.views

import com.jfoenix.controls.*
import com.lightningkite.kommunicate.HttpClient
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.MousePosition
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.*
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.implementationhelpers.*
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.mapping
import com.lightningkite.reacktive.property.*
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.effect.DropShadow
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.web.WebView
import javafx.stage.PopupWindow
import javafx.stage.Stage
import javafx.util.StringConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.set

data class MaterialJavaFxViewFactory(
    override val theme: Theme,
    override val colorSet: ColorSet = theme.main,
    val resourceFetcher: (String) -> InputStream,
    val scale: Double = 1.0
) : ViewFactory<Node> {

    init {
        HttpClient.resultThread = { GlobalScope.launch(Dispatchers.UI) { it() } }
    }

    var Node.desiredMargins: DesiredMargins
        get() = AnyDesiredMargins.getOrPut(this) {
            val size = when (this) {
                is HBox, is VBox, is StackPane, is ScrollPane -> 0f
                else -> 8f
            }
            return DesiredMargins(size, size, size, size)
        }
        set(value) {
            AnyDesiredMargins[this] = value
        }

    override var Node.lifecycle: TreeObservableProperty
        get() = AnyLifecycles.getOrPut(this) { TreeObservableProperty() }
        set(value) {
            AnyLifecycles[this] = value
        }

    override fun withColorSet(colorSet: ColorSet) = copy(colorSet = colorSet)

    val TextSize.javafx
        get() = when (this) {
            TextSize.Tiny -> 10.0 * scale
            TextSize.Body -> 14.0 * scale
            TextSize.Subheader -> 18.0 * scale
            TextSize.Header -> 24.0 * scale
        }

    fun <T> ObservableProperty<Boolean>.bindBidirectional(
        kotlinx: MutableObservableProperty<T>,
        property: Property<T>
    ) {
        bind(kotlinx) {
            if (it != property.value) {
                property.value = it
            }
        }
        property.addListener { observable, oldValue, newValue ->
            if (newValue != kotlinx.value) {
                kotlinx.value = newValue
            }
        }
    }

    fun <T> ObservableProperty<Boolean>.bindBidirectional(
        kotlinx: MutableObservableProperty<T>,
        property: ReadOnlyProperty<T>,
        write: (T) -> Unit
    ) {
        bind(kotlinx) {
            if (it != property.value) {
                write(it)
            }
        }
        property.addListener { observable, oldValue, newValue ->
            if (newValue != kotlinx.value) {
                kotlinx.value = newValue
            }
        }
    }

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ): Node = Label().apply {
        font = Font.font(size.javafx)
        textFill = when (importance) {
            Importance.Low -> colorSet.foregroundDisabled.toJavaFX()
            Importance.Normal -> colorSet.foreground.toJavaFX()
            Importance.High -> colorSet.foregroundHighlighted.toJavaFX()
            Importance.Danger -> Color.red.toJavaFX()
        }
        alignment = align.javafx
        isWrapText = true

        lifecycle.bind(text) {
            if(maxLines != Int.MAX_VALUE) {
                val cap = maxLines * 80
                this.text = if(it.length > cap) it.take(cap) + "..." else it
            } else {
                this.text = it
            }
        }
    }

    override fun horizontal(vararg views: Pair<LinearPlacement, Node>) = HBox().apply {
        this.spacing = 0.0
        val parent = this
        views.forEachIndexed { index, (placement, subview) ->
            children += frame(alignPair(Align.Fill, placement.align) to subview).apply {
                this.lifecycle.parent = parent.lifecycle
                maxHeight = Double.MAX_VALUE
                if (placement.weight > 0f) {
                    maxWidth = Double.MAX_VALUE
                    HBox.setHgrow(this, Priority.ALWAYS)
                }
                subview.desiredMargins.let {
                    val otherNextMargin = views.getOrNull(index + 1)?.second?.desiredMargins?.left
                    val finalNextMargin = otherNextMargin?.let { other -> Math.max(it.right, other) } ?: it.right
                    HBox.setMargin(
                        this, Insets(
                            it.top * scale,
                            finalNextMargin * scale,
                            it.bottom * scale,
                            if (index == 0) it.left * scale else 0.0
                        )
                    )
                }
            }
        }
    }

    override fun vertical(vararg views: Pair<LinearPlacement, Node>) = VBox().apply {
        this.spacing = 0.0
        val parent = this
        views.forEachIndexed { index, (placement, subview) ->
            children += frame(alignPair(placement.align, Align.Fill) to subview).apply {
                this.lifecycle.parent = parent.lifecycle
                maxWidth = Double.MAX_VALUE
                if (placement.weight > 0f) {
                    maxHeight = Double.MAX_VALUE
                    VBox.setVgrow(this, Priority.ALWAYS)
                }
                subview.desiredMargins.let {
                    val otherNextMargin = views.getOrNull(index + 1)?.second?.desiredMargins?.top
                    val finalNextMargin = otherNextMargin?.let { other -> Math.max(it.bottom, other) } ?: it.bottom
                    VBox.setMargin(
                        this, Insets(
                            if (index == 0) it.top * scale else 0.0,
                            it.right * scale,
                            finalNextMargin * scale,
                            it.left * scale
                        )
                    )
                }
            }
        }
    }

    override fun frame(vararg views: Pair<AlignPair, Node>) = StackPane().apply {
        val parent = this
        for ((placement, view) in views) {
            children += view.apply {
                this.lifecycle.parent = parent.lifecycle
                StackPane.setAlignment(view, placement.javafx)
                this.desiredMargins.let {
                    StackPane.setMargin(
                        this, Insets(
                            it.top * scale,
                            it.right * scale,
                            it.bottom * scale,
                            it.left * scale
                        )
                    )
                }
                (this as? Region)?.let {
                    if (placement.vertical == Align.Fill) {
                        it.maxHeight = Double.MAX_VALUE
                    }
                    if (placement.horizontal == Align.Fill) {
                        it.maxWidth = Double.MAX_VALUE
                    }
                }
            }
        }
    }

    override fun imageButton(
            imageWithSizing: ObservableProperty<ImageWithSizing>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ) = JFXButton().apply {
        val parent = this
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = image(imageWithSizing).apply {
            this.lifecycle.parent = parent.lifecycle
        }
        setOnAction {
            onClick.invoke()
        }
    }


    override fun <DEPENDENCY> window(
        dependency: DEPENDENCY,
        stack: MutableObservableList<ViewGenerator<DEPENDENCY, Node>>,
        tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, Node>>>
    ): Node = defaultLargeWindow(
        theme = theme,
        barBuilder = withColorSet(theme.bar),
        dependency = dependency,
        stack = stack,
        tabs = tabs
    )

    override fun <DEPENDENCY> pages(
        dependency: DEPENDENCY,
        page: MutableObservableProperty<Int>,
        vararg pageGenerator: ViewGenerator<DEPENDENCY, Node>
    ): Node = defaultPages(
        buttonColor = theme.main.foreground,
        dependency = dependency,
        page = page,
        pageGenerator = *pageGenerator
    )

    override fun tabs(
        options: ObservableList<TabItem>,
        selected: MutableObservableProperty<TabItem>
    ) = JFXTabPane().apply {

        //TODO: Color
//        style = "-fx-background-color: ${colorSet.background.toWeb()}"

        options.mapping {
            Tab(
                it.text,
                image(
                    ConstantObservableProperty(it.imageWithSizing)
                )
            )
        }.bindToJavaFX(lifecycle, tabs)

        lifecycle.bindBidirectional<Tab>(
            selected.transform(
                mapper = { tabs[options.indexOf(it)] },
                reverseMapper = { options[tabs.indexOf(it)] }
            ),
            this.selectionModel.selectedItemProperty()
        ) { it: Tab ->
            selectionModel.select(it)
        }
    }

    override fun <T> list(
        data: ObservableList<T>,
        firstIndex: MutableObservableProperty<Int>,
        lastIndex: MutableObservableProperty<Int>,
        direction: Direction,
        makeView: (obs: ObservableProperty<T>) -> Node
    ): Node = defaultList(
        pageSize = 20,
        buttonColor = colorSet.foreground,
        data = data,
        firstIndex = firstIndex,
        lastIndex = lastIndex,
        direction = direction,
        makeView = makeView
    )

    override fun work(view: Node, isWorking: ObservableProperty<Boolean>): Node {
        val spinner = JFXSpinner().apply {
            style = "-fx-stroke: ${colorSet.foreground.toWeb()}"
            isVisible = true
            minWidth = 30.0 * scale
            minHeight = 30.0 * scale
            prefWidth = 30.0 * scale
            prefHeight = 30.0 * scale
        }
        return swap(
            view = isWorking.transform {
                val nextView = if (it) spinner else view
                nextView to Animation.Fade
            }
        )
    }

    override fun progress(view: Node, progress: ObservableProperty<Float>): Node {
        val bar = JFXProgressBar().apply {
            style = "-fx-stroke: ${colorSet.foreground.toWeb()}"
            lifecycle.bind(progress) {
                this.progress = it.toDouble()
            }
        }
        return swap(
            view = progress.transform {
                val nextView = if (it == 1f) view else bar
                nextView to Animation.Fade
            }
        )
    }

    override fun image(imageWithSizing: ObservableProperty<ImageWithSizing>) = ImageView().apply {
        lifecycle.bind(imageWithSizing) {
            GlobalScope.launch(Dispatchers.UI) {
                it.defaultSize?.x?.times(scale)?.let { this@apply.fitWidth = it }
                it.defaultSize?.y?.times(scale)?.let { this@apply.fitHeight = it }
                //TODO: Scale type
                this@apply.image = it.image.get(scale.toFloat(), it.defaultSize)
            }
        }
    }

    override fun button(
            label: ObservableProperty<String>,
            imageWithSizing: ObservableProperty<ImageWithSizing?>,
            importance: Importance,
            onClick: () -> Unit
    ) = JFXButton().apply {
        this.buttonType = if (importance == Importance.Low) JFXButton.ButtonType.FLAT else JFXButton.ButtonType.RAISED

        val colorSet = theme.importance(importance)

        background = Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
        textFill = colorSet.foreground.toJavaFX()
        font = Font.font(TextSize.Body.javafx)
        lifecycle.bind(label) {
            text = it
        }
        setOnAction {
            onClick.invoke()
        }
    }

    override fun entryContext(
            label: String,
            help: String?,
            icon: ImageWithSizing?,
            feedback: ObservableProperty<Pair<Importance, String>?>,
            field: Node
    ): Node = defaultEntryContext(label, help, icon, feedback, field)

    fun toggleButton(
            imageWithSizing: ObservableProperty<ImageWithSizing?>,
            label: ObservableProperty<String?>,
            value: MutableObservableProperty<Boolean>
    ) = JFXButton().apply {
        this.buttonType = JFXButton.ButtonType.RAISED
        textFill = colorSet.foreground.toJavaFX()
        lifecycle.bind(label) {
            text = it
        }
        lifecycle.bind(value) {
            background = if (it) {
                Background(BackgroundFill(colorSet.backgroundHighlighted.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
            } else {
                Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
            }
        }
        setOnAction {
            value.value != value.value
        }
    }

    override fun <T> picker(
        options: ObservableList<T>,
        selected: MutableObservableProperty<T>,
        makeView: (obs: ObservableProperty<T>) -> Node
    ) = JFXComboBox<T>().apply {

        focusColor = colorSet.backgroundHighlighted.toJavaFX()
        unFocusColor = colorSet.background.toJavaFX()
        items = options.asJavaFX(lifecycle)

        setCellFactory {
            object : ListCell<T>() {
                val obs = StandardObservableProperty<T>(options.firstOrNull() as T)

                init {
                    children += makeView(obs)
                }

                override fun updateItem(item: T, empty: Boolean) {
                    obs.value = item
                }
            }
        }
    }

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Node =
        if (type == TextInputType.Password) JFXPasswordField().apply {
            this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
            font = Font.font(TextSize.Body.javafx)
            this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
            this.unFocusColor = colorSet.foreground.toJavaFX()
            lifecycle.bindBidirectional(text, this.textProperty())
            this.isLabelFloat = true
        } else JFXTextField().apply {
            this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
            font = Font.font(TextSize.Body.javafx)
            this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
            this.unFocusColor = colorSet.foreground.toJavaFX()
            lifecycle.bindBidirectional(text, this.textProperty())
            this.isLabelFloat = true
        }

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Node =
        JFXTextArea().apply {
            this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
            font = Font.font(TextSize.Body.javafx)
            this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
            this.unFocusColor = colorSet.foreground.toJavaFX()
            lifecycle.bindBidirectional(text, this.textProperty())
            this.isLabelFloat = true
            minHeight = scale * 100.0
        }

    override fun numberField(
        value: MutableObservableProperty<Number?>,
        placeholder: String,
        type: NumberInputType,
        decimalPlaces: Int
    ): Node = JFXTextField().apply {
        this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
        font = Font.font(TextSize.Body.javafx)
        this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
        this.unFocusColor = colorSet.foreground.toJavaFX()

        val compareVal = Math.pow(-decimalPlaces.toDouble(), 10.0) / 2
        val converter = object : StringConverter<Number>() {
            override fun toString(value: Number?): String =
                if (value == null) "" else DecimalFormat("#." + "#".repeat(decimalPlaces)).format(value)

            override fun fromString(string: String?): Number? = string?.toDoubleOrNull()
        }
        textFormatter = TextFormatter(converter)
        lifecycle.bind(value) {
            val backing = it
            val converted = converter.fromString(text)
            val different =
                (backing == null && converted != null) ||
                        (backing != null && converted == null) ||
                        (backing != null && converted != null && backing.toDouble().minus(converted.toDouble())
                            .let { Math.abs(it) > compareVal })
            if (different) {
                text = converter.toString(backing)
            }
        }
        textProperty().addListener { _, _, newValue ->
            val backing = value.value
            val converted = converter.fromString(newValue)
            val different =
                (backing == null && converted != null) ||
                        (backing != null && converted == null) ||
                        (backing != null && converted != null && backing.toDouble().minus(converted.toDouble())
                            .let { Math.abs(it) > compareVal })
            if (different) {
                value.value = converter.fromString(newValue)
            }
        }
        this.isLabelFloat = true
    }

    override fun datePicker(observable: MutableObservableProperty<Date>) = JFXDatePicker().apply {
        this.editor.font = Font.font(TextSize.Body.javafx)
        this.editor.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
        this.defaultColor = colorSet.foreground.toJavaFX()
        this.value = LocalDate.ofEpochDay(observable.value.daysSinceEpoch.toLong())
        lifecycle.bindBidirectional<LocalDate>(
            kotlinx = observable.transform(
                mapper = { LocalDate.ofEpochDay(it.daysSinceEpoch.toLong()) },
                reverseMapper = { Date(it.toEpochDay().toInt()) }
            ),
            property = valueProperty()
        )
    }

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>): Node = horizontal(
        LinearPlacement.fillFill to datePicker(observable = observable.transform(
            mapper = { it.date },
            reverseMapper = { observable.value.copy(date = it) }
        )),
        LinearPlacement.wrapFill to space(Point(8f, 8f)),
        LinearPlacement.fillFill to timePicker(observable = observable.transform(
            mapper = { it.time },
            reverseMapper = { observable.value.copy(time = it) }
        ))
    )

    override fun timePicker(observable: MutableObservableProperty<Time>) = JFXTimePicker().apply {
        this.editor.font = Font.font(TextSize.Body.javafx)
        this.editor.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
        this.defaultColor = colorSet.foreground.toJavaFX()
        this.value = LocalTime.ofNanoOfDay(observable.value.millisecondsSinceMidnight.times(1000000L))
        lifecycle.bindBidirectional<LocalTime>(
            kotlinx = observable.transform(
                mapper = { LocalTime.ofNanoOfDay(it.millisecondsSinceMidnight.times(1000000L)) },
                reverseMapper = { Time(it.toNanoOfDay().div(1000000L).toInt()) }
            ),
            property = valueProperty()
        )
    }

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>) = JFXSlider().apply {
        min = range.start.toDouble()
        max = range.endInclusive.toDouble()
        lifecycle.bindBidirectional(observable.transform(
            mapper = { it as Number },
            reverseMapper = { it.toInt() }
        ), valueProperty())
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>) = JFXCheckBox().apply {
        lifecycle.bindBidirectional(observable, selectedProperty())
    }

    override fun refresh(contains: Node, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): Node = frame(
        AlignPair.FillFill to contains,
        AlignPair.TopRight to work(space(Point(20f, 20f)), working)
    )

    override fun scrollVertical(view: Node, amount: MutableObservableProperty<Float>): Node = ScrollPane(view).apply {
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        isFitToWidth = true
        isFitToHeight = true
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        view.lifecycle.parent = this.lifecycle

        var suppress = false
        lifecycle.bind(amount) {
            suppress = true
            vvalue = it.toDouble()
        }
        this.setOnScrollFinished {
            if (suppress) {
                suppress = false
                return@setOnScrollFinished
            }
            amount.value = vvalue.toFloat()
        }

    }

    override fun scrollHorizontal(view: Node, amount: MutableObservableProperty<Float>): Node = ScrollPane(view).apply {
        hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        isFitToWidth = true
        isFitToHeight = true
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        view.lifecycle.parent = this.lifecycle

        var suppress = false
        lifecycle.bind(amount) {
            suppress = true
            hvalue = it.toDouble()
        }
        this.setOnScrollFinished {
            if (suppress) {
                suppress = false
                return@setOnScrollFinished
            }
            amount.value = hvalue.toFloat()
        }

    }

    override fun scrollBoth(
        view: Node,
        amountX: MutableObservableProperty<Float>,
        amountY: MutableObservableProperty<Float>
    ): Node = ScrollPane(view).apply {
        hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        isFitToWidth = true
        isFitToHeight = true
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        view.lifecycle.parent = this.lifecycle

        var suppress = false
        lifecycle.bind(amountX) {
            suppress = true
            hvalue = it.toDouble()
        }
        lifecycle.bind(amountY) {
            suppress = true
            vvalue = it.toDouble()
        }
        this.setOnScrollFinished {
            if (suppress) {
                suppress = false
                return@setOnScrollFinished
            }
            amountX.value = hvalue.toFloat()
            amountY.value = vvalue.toFloat()
        }

    }

    override fun swap(view: ObservableProperty<Pair<Node, Animation>>) = StackPane().apply {
        val parent = this
        var currentView: Node? = null

        background = Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))

        val clipRect = Rectangle()
        layoutBoundsProperty().addListener { observable, oldValue, newValue ->
            clipRect.width = newValue.width
            clipRect.height = newValue.height
        }
        clip = clipRect

        lifecycle.bind(view) { (view, animation) ->
            GlobalScope.launch(Dispatchers.UI) {
                val containerSize = Point(
                    width.toFloat(),
                    height.toFloat()
                )
                currentView?.let { old ->
                    animation.javaFxOut(old, containerSize).apply {
                        setOnFinished {
                            old.lifecycle.parent = null
                            children.remove(old)
                        }
                    }.play()
                }

                children += view.apply {
                    this.lifecycle.parent = parent.lifecycle
                    StackPane.setAlignment(view, AlignPair.CenterCenter.javafx)
                    this.desiredMargins.let {
                        StackPane.setMargin(
                            this, Insets(
                                it.top * scale,
                                it.right * scale,
                                it.bottom * scale,
                                it.left * scale
                            )
                        )
                    }
                    if (currentView != null) {
                        animation.javaFxIn(this, containerSize).play()
                    }

                }
                currentView = view
            }
        }
    }

    override fun space(size: Point) = Region().apply {
        prefWidth = size.x.times(scale)
        prefHeight = size.y.times(scale)
    }

    override fun web(content: ObservableProperty<String>) = WebView().apply {
        lifecycle.bind(content) {
            if (it.startsWith("http")) {
                engine.load(it)
            } else {
                engine.loadContent(it)
            }
        }
    }

    override fun Node.setWidth(width: Float): Node {
        prefWidth(width.toDouble())
        return this
    }

    override fun Node.setHeight(height: Float): Node {
        prefHeight(height.toDouble())
        return this
    }

    override fun Node.margin(left: Float, top: Float, right: Float, bottom: Float) = this.apply {
        this.desiredMargins = DesiredMargins(left, top, right, bottom)
    }


    override fun Node.background(color: ObservableProperty<Color>) = this.apply {
        if (this is Region) {
            lifecycle.bind(color) {
                this.background = Background(BackgroundFill(it.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
            }
        }
    }

    override fun card(view: Node): Node = frame(AlignPair.FillFill to view).apply {
        desiredMargins = DesiredMargins(8f)
        background = Background(
            BackgroundFill(
                colorSet.backgroundHighlighted.toJavaFX(),
                CornerRadii(4.0 * scale),
                Insets.EMPTY
            )
        )
        effect = DropShadow(3.0 * scale, 0.0, 1.0 * scale, Color.black.copy(alpha = .5f).toJavaFX())
        padding = Insets(8.0 * scale)
    }

    override fun Node.alpha(alpha: ObservableProperty<Float>) = this.apply {
        lifecycle.bind(alpha) {
            this.opacity = it.toDouble()
            isVisible = it != 0f
        }
    }

    override fun Node.clickable(onClick: () -> Unit) = this.apply {
        setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                onClick.invoke()
            }
        }
    }

    override fun Node.altClickable(onAltClick: () -> Unit): Node = this.apply {
        setOnContextMenuRequested {
            onAltClick.invoke()
        }
    }

    override fun launchDialog(
        dismissable: Boolean,
        onDismiss: () -> Unit,
        makeView: (dismissDialog: () -> Unit) -> Node
    ) {
        val dialog = Stage()
        MousePosition.init(dialog)
        var dismisser = {}
        val view = makeView { dismisser() }
        view.lifecycle.alwaysOn = true
        dialog.scene = Scene(view as Parent)
        dialog.show()
        dismisser = {
            dialog.close()
            onDismiss.invoke()
        }
        dialog.setOnCloseRequest {
            onDismiss.invoke()
        }
    }

    override fun launchSelector(title: String?, options: List<Pair<String, () -> Unit>>) {
        val contextMenu = ContextMenu()
        for (option in options) {
            contextMenu.items.add(MenuItem(option.first).apply {
                setOnAction {
                    option.second.invoke()
                }
            })
        }
        ApplicationAccess.stage.scene?.window?.let { window ->
            println(MousePosition.point)
            contextMenu.anchorLocation = PopupWindow.AnchorLocation.CONTENT_TOP_LEFT
            contextMenu.show(window, MousePosition.point.x.toDouble(), MousePosition.point.y.toDouble())
        } ?: println("WHAT!  IT'S NULL!")
    }
}
