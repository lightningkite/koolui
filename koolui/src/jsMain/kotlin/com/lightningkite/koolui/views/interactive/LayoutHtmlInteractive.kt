package com.lightningkite.koolui.views.interactive

import com.lightningkite.kommon.asInt8Array
import com.lightningkite.koolui.appendLifecycled
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextInputType
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.*
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.makeElement
import com.lightningkite.koolui.removeLifecycled
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.Touch
import com.lightningkite.koolui.views.onNewTouch
import com.lightningkite.koolui.views.toCssClass
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import kotlin.browser.document
import kotlin.dom.addClass

interface LayoutHtmlInteractive : ViewFactoryInteractiveDefault<Layout<*, HTMLElement>>, LayoutViewWrapper<HTMLElement>, Themed {
    override fun button(
            label: ObservableProperty<String>,
            imageWithOptions: ObservableProperty<ImageWithOptions?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLButtonElement>("button")){ layout ->
        addClass(importance.toCssClass())
        type = "button"

        val textNode = usingColorSet(theme.importance(importance)){
            text(label, importance, align = AlignPair.CenterCenter)
        }
        layout.addChild(textNode)

        val imageNode: Layout<*, HTMLElement> by lazy {
            image(imageWithOptions.transform {
                it ?: MaterialIcon.android.color(Color.white).withOptions()
            })
        }
        var isImageAdded = false
        layout.isAttached.bind(imageWithOptions) {
            if (it == null) {
                if (isImageAdded) {
                    layout.removeChild(imageNode)
                    isImageAdded = false
                }
            } else {
                if (!isImageAdded) {
                    layout.addChild(imageNode)
                    isImageAdded = true
                }
            }
        }
        onclick = {
            onClick.invoke()
        }
    }

    override fun imageButton(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?>,
            importance: Importance,
            onClick: () -> Unit
    ): Layout<*, HTMLElement>  = intrinsicLayout(makeElement<HTMLButtonElement>("button")){ layout ->
        addClass(importance.toCssClass(), "ImageFocused")
        type = "button"

        layout.addChild(image(imageWithOptions))

        val textNode: Layout<*, HTMLElement> by lazy {
            usingColorSet(theme.importance(importance)) {
                text(label.transform {
                    it ?: ""
                }, importance, align = AlignPair.CenterCenter)
            }

        }
        var isTextAdded = false
        layout.isAttached.bind(label) {
            if (it == null) {
                if (isTextAdded) {
                    layout.removeChild(textNode)
                    isTextAdded = false
                }
            } else {
                if (!isTextAdded) {
                    layout.addChild(textNode)
                    isTextAdded = true
                }
            }
        }
        onclick = {
            onClick.invoke()
        }
    }

    override fun <T> picker(
            options: ObservableList<T>,
            selected: MutableObservableProperty<T>,
            toString: (T) -> String
    ): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLSelectElement>("select")) { layout ->
        layout.isAttached.bind(options.onListUpdate) {
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
        layout.isAttached.bind(selected) { selectedValue ->
            selectedIndex = options.indexOf(selectedValue).takeUnless { it == -1 } ?: 0
        }
    }

    override fun textField(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLInputElement>("input")) { layout ->
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
        layout.isAttached.bind(text) { textValue ->
            if (this.value != textValue) {
                this.value = textValue
            }
        }
    }

    override fun textArea(
            text: MutableObservableProperty<String>,
            placeholder: String,
            type: TextInputType
    ): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLTextAreaElement>("textarea")) { layout ->
        this.placeholder = placeholder
        this.oninput = { _ ->
            if (this.value != text.value) {
                text.value = this.value
            }
        }
        layout.isAttached.bind(text) { textValue ->
            if (this.value != textValue) {
                this.value = textValue
            }
        }
    }

    override fun integerField(
            value: MutableObservableProperty<Long?>,
            placeholder: String,
            allowNegatives: Boolean
    ): Layout<*, HTMLElement>  = intrinsicLayout(makeElement<HTMLInputElement>("textarea")) { layout ->
        this.placeholder = placeholder
        this.type = "number"
        if (!allowNegatives) min = "0"
        step = "1"
        this.oninput = { _ ->
            if (valueAsNumber.toLong() != value.value) {
                value.value = valueAsNumber.toLong()
            }
        }
        layout.isAttached.bind(value) { value ->
            if (valueAsNumber.toLong() != value) {
                valueAsNumber = value?.toDouble() ?: 0.0
            }
        }
    }

    override fun numberField(
            value: MutableObservableProperty<Double?>,
            placeholder: String,
            allowNegatives: Boolean,
            decimalPlaces: Int
    ): Layout<*, HTMLElement>  = intrinsicLayout(makeElement<HTMLInputElement>("textarea")) { layout ->
        this.placeholder = placeholder
        this.type = "number"
        if (!allowNegatives) min = "0"
        step = if (decimalPlaces >= 1) "." + "0".repeat(decimalPlaces - 1) + "1" else "1"
        this.oninput = { _ ->
            if (valueAsNumber != value.value) {
                value.value = valueAsNumber
            }
        }
        layout.isAttached.bind(value) { value ->
            if (valueAsNumber != value) {
                valueAsNumber = value ?: 0.0
            }
        }
    }

    override fun slider(
            range: IntRange,
            observable: MutableObservableProperty<Int>
    ): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLInputElement>("input")) { layout ->
        this.placeholder = placeholder

        this.type = "range"
        this.min = range.start.toString()
        this.max = range.endInclusive.toString()
        this.oninput = { _ ->
            if (this.valueAsNumber.toInt() != observable.value) {
                observable.value = (this.valueAsNumber.toInt())
            }
        }
        layout.isAttached.bind(observable) {
            val value = observable.value
            if (this.valueAsNumber.toInt() != value) {
                this.valueAsNumber = value.toDouble()
            }
        }
    }

    override fun toggle(observable: MutableObservableProperty<Boolean>): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLInputElement>("input")) { layout ->
        this.type = "checkbox"
        this.oninput = { _ ->
            if (this.checked != observable.value) {
                observable.value = (this.checked)
            }
        }
        layout.isAttached.bind(observable) {
            val value = observable.value
            if (this.checked != value) {
                this.checked = value
            }
        }
    }

    override fun Layout<*, HTMLElement>.touchable(onNewTouch: (Touch) -> Unit): Layout<*, HTMLElement> {
        this.viewAsBase.onNewTouch(onNewTouch)
        return this
    }

    override fun Layout<*, HTMLElement>.clickable(onClick: () -> Unit): Layout<*, HTMLElement> {
        viewAsBase.onclick = {
            it.stopPropagation()
            onClick.invoke()
        }
        return this
    }

    override fun Layout<*, HTMLElement>.altClickable(onAltClick: () -> Unit): Layout<*, HTMLElement> {
        viewAsBase.oncontextmenu = {
            it.stopPropagation()
            onAltClick.invoke()
        }
        return this
    }

    override fun Layout<*, HTMLElement>.acceptCharacterInput(onCharacter: (Char) -> Unit, keyboardType: KeyboardType): Layout<*, HTMLElement> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}