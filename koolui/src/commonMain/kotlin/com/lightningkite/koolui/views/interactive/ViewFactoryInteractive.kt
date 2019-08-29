package com.lightningkite.koolui.views.interactive

import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextInputType
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.views.Touch
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.DateTime
import com.lightningkite.lokalize.time.Time
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

interface ViewFactoryInteractive<VIEW> {

    /**
     * A button with the given image and label.  Runs [onClick] when the button is interacted with.
     */
    fun button(
            label: ObservableProperty<String>,
            imageWithOptions: ObservableProperty<ImageWithOptions?> = ConstantObservableProperty(null),
            importance: Importance = Importance.Normal,
            onClick: () -> Unit
    ): VIEW

    /**
     * A button with the given image and label.  Runs [onClick] when the button is interacted with.
     * Attempts to use image over text.
     */
    fun imageButton(
            imageWithOptions: ObservableProperty<ImageWithOptions>,
            label: ObservableProperty<String?> = ConstantObservableProperty(null),
            importance: Importance = Importance.Normal,
            onClick: () -> Unit
    ): VIEW

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
            icon: ImageWithOptions? = null,
            feedback: ObservableProperty<Pair<Importance, String>?> = ConstantObservableProperty(null),
            field: VIEW
    ): VIEW

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
    ): VIEW

    /**
     * An element that allows the user to enter a date and time.
     */
    fun dateTimePicker(
            observable: MutableObservableProperty<DateTime>
    ): VIEW

    /**
     * An element that allows the user to enter a time.
     */
    fun timePicker(
            observable: MutableObservableProperty<Time>
    ): VIEW

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

    /**
     * Detect and use touches/clicks on the view directly, overriding any other touch functionality.
     */
    fun VIEW.touchable(
            onNewTouch: (Touch) -> Unit
    ): VIEW

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
     * Makes this element focusable for keyboard input
     */
    fun VIEW.acceptCharacterInput(
            onCharacter: (Char) -> Unit,
            keyboardType: KeyboardType = KeyboardType.All
    ): VIEW

    /**
     * Wraps content with a control to refresh the data contained within.
     * On mobile, enables a "swipe to refresh" gesture.
     */
    fun refresh(
            contains: VIEW,
            working: ObservableProperty<Boolean>,
            onRefresh: () -> Unit
    ): VIEW// = defaultRefresh(contains, working, onRefresh)
}

