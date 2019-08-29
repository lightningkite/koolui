package com.lightningkite.koolui.views.interactive

import com.jfoenix.controls.*
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.async.scope
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextInputType
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.wrap
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.transform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ListCell
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.text.Font
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface LayoutJavaFxInteractive: ViewFactoryInteractiveDefault<Layout<*, Node>>, ViewFactoryBasic<Layout<*, Node>>, LayoutViewWrapper<Node>, Themed, HasScale {

    override fun button(label: ObservableProperty<String>, imageWithOptions: ObservableProperty<ImageWithOptions?>, importance: Importance, onClick: () -> Unit): Layout<*, Node> = intrinsicLayout(JFXButton()) { layout ->
        this.buttonType = if (importance == Importance.Low) JFXButton.ButtonType.FLAT else JFXButton.ButtonType.RAISED
        if(importance > Importance.Low) {
            val colorSet = theme.importance(importance)
            background = Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
            textFill = colorSet.foreground.toJavaFX()
        }
        font = Font.font(TextSize.Body.javafx)
        layout.isAttached.bind(label) {
            text = it
            layout.requestMeasurement()
        }
        setOnAction {
            onClick.invoke()
        }
    }

    override fun imageButton(imageWithOptions: ObservableProperty<ImageWithOptions>, label: ObservableProperty<String?>, importance: Importance, onClick: () -> Unit): Layout<*, Node> = intrinsicLayout(JFXButton()) { layout ->
        val parent = this
        contentDisplay = ContentDisplay.GRAPHIC_ONLY

        this.buttonType = if (importance == Importance.Low) JFXButton.ButtonType.FLAT else JFXButton.ButtonType.RAISED
        if(importance > Importance.Low) {
            val colorSet = theme.importance(importance)
            background = Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
        }

        val g = ImageView()
        graphic = g

        layout.isAttached.bind(imageWithOptions) {
            layout.isAttached.scope.launch(Dispatchers.UI) {
                it.defaultSize?.x?.times(scale)?.let { g.fitWidth = it }
                it.defaultSize?.y?.times(scale)?.let { g.fitHeight = it }
                //TODO: Scale type
                g.image = it.image.get(scale.toFloat(), it.defaultSize)

                layout.requestMeasurement()
            }
        }
        setOnAction {
            onClick.invoke()
        }
    }

    override fun <T> picker(options: ObservableList<T>, selected: MutableObservableProperty<T>, toString: (T) -> String): Layout<*, Node> = wrap(JFXComboBox<T>()) { lifecycle ->
        focusColor = colorSet.backgroundHighlighted.toJavaFX()
        unFocusColor = colorSet.background.toJavaFX()

        lifecycle.bind(options.onListUpdate){
            items = FXCollections.observableList(it)
            this.selectionModel.select(selected.value)
        }

        var suppress = false
        lifecycle.bind(selected){
            if(suppress) return@bind
            suppress = true
            this.selectionModel.select(it)
            suppress = false
        }
        this.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            if(suppress) return@addListener
            suppress = true
            selected.value = newValue
            suppress = false
        }

        setCellFactory {
            object : ListCell<T>() {
                init {
                    font = Font.font(TextSize.Body.javafx)
                    textFill = colorSet.foreground.toJavaFX()
                    background = Background(BackgroundFill(colorSet.background.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
                }

                val obs = StandardObservableProperty<T>(options.firstOrNull() as T)

                override fun updateItem(item: T, empty: Boolean) {
                    this.text = toString(item)
                    super.updateItem(item, empty)
                }

            }
        }
        this.buttonCell = cellFactory.call(null)
    }

    override fun textField(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, Node> {
        return if (type == TextInputType.Password) wrap(JFXPasswordField()){ lifecycle ->
            this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
            font = Font.font(TextSize.Body.javafx)
            this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
            this.unFocusColor = colorSet.foreground.toJavaFX()
            lifecycle.bindBidirectional(text, this.textProperty())
            this.isLabelFloat = true
        } else wrap(JFXTextField()){ lifecycle ->
            this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
            font = Font.font(TextSize.Body.javafx)
            this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
            this.unFocusColor = colorSet.foreground.toJavaFX()
            lifecycle.bindBidirectional(text, this.textProperty())
            this.isLabelFloat = true
        }
    }

    override fun textArea(text: MutableObservableProperty<String>, placeholder: String, type: TextInputType): Layout<*, Node> = wrap(JFXTextArea())  { lifecycle ->
        this.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
        font = Font.font(TextSize.Body.javafx)
        this.focusColor = colorSet.foregroundHighlighted.toJavaFX()
        this.unFocusColor = colorSet.foreground.toJavaFX()
        lifecycle.bindBidirectional(text, this.textProperty())
        this.isLabelFloat = true
        minHeight = scale * 100.0
    }

//    override fun datePicker(observable: MutableObservableProperty<Date>): Layout<*, Node> = wrap(JFXDatePicker()) { lifecycle ->
//        this.editor.font = Font.font(TextSize.Body.javafx)
//        this.editor.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
//        this.defaultColor = colorSet.foreground.toJavaFX()
//        this.value = LocalDate.ofEpochDay(observable.value.daysSinceEpoch.toLong())
//        lifecycle.bindBidirectional<LocalDate>(
//                kotlinx = observable.transform(
//                        mapper = { LocalDate.ofEpochDay(it.daysSinceEpoch.toLong()) },
//                        reverseMapper = { Date(it.toEpochDay().toInt()) }
//                ),
//                property = valueProperty()
//        )
//    }
//
//    override fun timePicker(observable: MutableObservableProperty<Time>): Layout<*, Node> = wrap(JFXTimePicker()) { lifecycle ->
//        this.editor.font = Font.font(TextSize.Body.javafx)
//        this.editor.style = "-fx-text-fill: ${colorSet.foreground.toWeb()}"
//        this.defaultColor = colorSet.foreground.toJavaFX()
//        this.value = LocalTime.ofNanoOfDay(observable.value.millisecondsSinceMidnight.times(1000000L))
//        lifecycle.bindBidirectional<LocalTime>(
//                kotlinx = observable.transform(
//                        mapper = { LocalTime.ofNanoOfDay(it.millisecondsSinceMidnight.times(1000000L)) },
//                        reverseMapper = { Time(it.toNanoOfDay().div(1000000L).toInt()) }
//                ),
//                property = valueProperty()
//        )
//    }

    override fun slider(range: IntRange, observable: MutableObservableProperty<Int>): Layout<*, Node> = wrap(JFXSlider()) { lifecycle ->
        min = range.start.toDouble()
        max = range.endInclusive.toDouble()
        lifecycle.bindBidirectional(observable.transform(
                mapper = { it as Number },
                reverseMapper = { it.toInt() }
        ), valueProperty())
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>): Layout<*, Node> = wrap(JFXCheckBox()) { lifecycle ->
        lifecycle.bindBidirectional(observable, selectedProperty())
    }

    override fun Layout<*, Node>.touchable(onNewTouch: (Touch) -> Unit): Layout<*, Node> {
        viewAsBase.setOnNewTouch(onNewTouch)
        return this
    }

    override fun Layout<*, Node>.clickable(onClick: () -> Unit): Layout<*, Node> {
        viewAsBase.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                onClick.invoke()
            }
        }
        return this
    }

    override fun Layout<*, Node>.altClickable(onAltClick: () -> Unit): Layout<*, Node> {
        viewAsBase.setOnContextMenuRequested {
            onAltClick.invoke()
        }
        return this
    }

    override fun Layout<*, Node>.acceptCharacterInput(onCharacter: (Char) -> Unit, keyboardType: KeyboardType): Layout<*, Node> {
        viewAsBase.isFocusTraversable = true
        viewAsBase.setOnKeyPressed { it.character.firstOrNull()?.let(onCharacter) }
        return this
    }
}