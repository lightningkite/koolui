package com.lightningkite.koolui.test

import com.lightningkite.lokalize.time.*
import com.lightningkite.recktangle.Point
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.withOptions
import com.lightningkite.koolui.image.color

class ControlsVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Controls"
    val alpha = StandardObservableProperty(0f)

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        scrollVertical(vertical {

//            -entryContext(
//                    label = "imageButton",
//                    field = imageButton(imageWithOptions = ConstantObservableProperty(MaterialIcon.android.color(Color.gray).withOptions(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Low, onClick = {})
//            )
//            -entryContext(
//                    label = "imageButton",
//                    field = imageButton(imageWithOptions = ConstantObservableProperty(MaterialIcon.android.color(Color.white).withOptions(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Normal, onClick = {})
//            )
//            -entryContext(
//                    label = "imageButton",
//                    field = imageButton(imageWithOptions = ConstantObservableProperty(MaterialIcon.android.color(Color.white).withOptions(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.High, onClick = {})
//            )
//            -entryContext(
//                    label = "imageButton",
//                    field = imageButton(imageWithOptions = ConstantObservableProperty(MaterialIcon.android.color(Color.white).withOptions(Point(24f, 24f))), label = ConstantObservableProperty("Button"), importance = Importance.Danger, onClick = {})
//            )
//
//            -entryContext(
//                    label = "button",
//                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Low, onClick = {})
//            )
//            -entryContext(
//                    label = "button",
//                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Normal, onClick = {})
//            )
//            -entryContext(
//                    label = "button",
//                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.High, onClick = {})
//            )
//            -entryContext(
//                    label = "button",
//                    field = button(label = ConstantObservableProperty("Button"), importance = Importance.Danger, onClick = {})
//            )
//            -entryContext(
//                    label = "textField",
//                    field = textField(text = StandardObservableProperty("TextField"))
//            )
//            -entryContext(
//                    label = "textArea",
//                    field = textArea(text = StandardObservableProperty("TextArea"))
//            )
//            -entryContext(
//                    label = "numberField",
//                    field = numberField(value = StandardObservableProperty(22.0))
//            )
//            -entryContext(
//                    label = "slider",
//                    field = slider(0..100, StandardObservableProperty(22))
//            )
//            -entryContext(
//                    label = "picker",
//                    field = picker(observableListOf("A", "B", "C"), StandardObservableProperty("A"))
//            )
//            -entryContext(
//                    label = "toggle",
//                    field = toggle(StandardObservableProperty(false))
//            )
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
