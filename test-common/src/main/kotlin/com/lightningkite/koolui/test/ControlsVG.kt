package com.lightningkite.koolui.test

import com.lightningkite.lokalize.*
import com.lightningkite.lokalize.time.*
import com.lightningkite.recktangle.Point
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.asImage
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class ControlsVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Controls"
    val alpha = StandardObservableProperty(0f)

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        scrollVertical(vertical {

            -entryContext(
                    label = "imageButton",
                    field = imageButton(image = ConstantObservableProperty(MaterialIcon.android.color(Color.gray).asImage(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Low, onClick = {})
            )
            -entryContext(
                    label = "imageButton",
                    field = imageButton(image = ConstantObservableProperty(MaterialIcon.android.color(Color.white).asImage(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Normal, onClick = {})
            )
            -entryContext(
                    label = "imageButton",
                    field = imageButton(image = ConstantObservableProperty(MaterialIcon.android.color(Color.white).asImage(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.High, onClick = {})
            )
            -entryContext(
                    label = "imageButton",
                    field = imageButton(image = ConstantObservableProperty(MaterialIcon.android.color(Color.white).asImage(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Danger, onClick = {})
            )

            -entryContext(
                    label = "button",
                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Low, onClick = {})
            )
            -entryContext(
                    label = "button",
                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Normal, onClick = {})
            )
            -entryContext(
                    label = "button",
                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.High, onClick = {})
            )
            -entryContext(
                    label = "button",
                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Danger, onClick = {})
            )
            -entryContext(
                    label = "textField",
                    field = textField(text = StandardObservableProperty("TextField"))
            )
            -entryContext(
                    label = "textArea",
                    field = textArea(text = StandardObservableProperty("TextArea"))
            )
            -entryContext(
                    label = "numberField",
                    field = numberField(value = StandardObservableProperty(22))
            )
            -entryContext(
                    label = "slider",
                    field = slider(0..100, StandardObservableProperty(22))
            )
            -entryContext(
                    label = "picker",
                    field = picker(observableListOf("A", "B", "C"), StandardObservableProperty("A")) {
                        text(it)
                    }
            )
            -entryContext(
                    label = "toggle",
                    field = toggle(StandardObservableProperty(false))
            )
            -entryContext(
                    label = "datePicker",
                    field = datePicker(StandardObservableProperty(TimeStamp.now().date()))
            )
            -entryContext(
                    label = "timePicker",
                    field = timePicker(StandardObservableProperty(TimeStamp.now().time()))
            )
            -entryContext(
                    label = "dateTimePicker",
                    field = dateTimePicker(StandardObservableProperty(DateTime(TimeStamp.now().date(), TimeStamp.now().time())))
            )
        })
    }
}
