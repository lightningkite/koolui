package com.lightningkite.koolui.views.interactive

import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.image.withOptions
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.views.graphics.*
import com.lightningkite.koolui.views.layout.horizontal
import com.lightningkite.lokalize.time.*
import com.lightningkite.reacktive.list.asObservableList
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform

interface ViewFactoryInteractiveDefault<VIEW> : Themed, ViewFactoryInteractive<VIEW>, ViewFactoryLayout<VIEW>, ViewFactoryBasic<VIEW> {

    override fun refresh(contains: VIEW, working: ObservableProperty<Boolean>, onRefresh: () -> Unit): VIEW = align {
        AlignPair.FillFill - contains
        AlignPair.TopRight - work(imageButton(
                imageWithOptions = MaterialIcon.refresh.color(colorSet.foreground).withOptions(),
                onClick = onRefresh
        ), working)
    }

    override fun entryContext(
            label: String,
            help: String?,
            icon: ImageWithOptions?,
            feedback: ObservableProperty<Pair<Importance, String>?>,
            field: VIEW
    ): VIEW = frame(horizontal {

        defaultAlign = Align.Center

        if (icon != null) {
            if(this is ViewFactoryGraphics<*>) {
                @Suppress("UNCHECKED_CAST")
                -(image(ConstantObservableProperty(icon)) as VIEW).margin(2f)
            }

            -space(4f)
        }

        +vertical {
            -text(ConstantObservableProperty(label), size = TextSize.Tiny).margin(2f)
            -field.margin(2f)
            -swap(
                    feedback.transform {
                        val v = if (it == null) space(0f).margin(0f)
                        else text(ConstantObservableProperty(it.second), importance = it.first, size = TextSize.Tiny).margin(2f)
                        v to Animation.Fade
                    }
            ).margin(0f)
        }
    })

    override fun dateTimePicker(observable: MutableObservableProperty<DateTime>) = horizontal(
            LinearPlacement.wrapFill to datePicker(observable.transform(
                    mapper = { it.date },
                    reverseMapper = { observable.value.copy(date = it) }
            )),
            LinearPlacement.wrapFill to timePicker(observable.transform(
                    mapper = { it.time },
                    reverseMapper = { observable.value.copy(time = it) }
            ))
    )

    override fun timePicker(observable: MutableObservableProperty<Time>): VIEW = horizontal {
        -integerField(observable.transform(
                mapper = { it.hours.toLong() as Long? },
                reverseMapper = {
                    if (it == null) observable.value
                    else observable.value.copy(hours = it.toInt())
                }
        ))
        -text(":")
        -integerField(observable.transform(
                mapper = { it.minutes.toLong() as Long? },
                reverseMapper = {
                    if (it == null) observable.value
                    else observable.value.copy(minutes = it.toInt())
                }
        ))
    }

    override fun datePicker(observable: MutableObservableProperty<Date>): VIEW = horizontal {
        -picker(
                options = Month.values().toList().asObservableList(),
                selected = observable.transform(
                        mapper = { it.month },
                        reverseMapper = {
                            observable.value.toMonthInYear(it)
                        }
                )
        )
        -picker(
                options = observable.transform { (1..it.month.days(it.year)).toList() }.asObservableList<Int>(),
                selected = observable.transform(
                        mapper = {
                            it.dayOfMonth
                        },
                        reverseMapper = {
                            observable.value.toDayInMonth(it)
                        }
                )
        )
        -integerField(observable.transform(
                mapper = { it.year.sinceAD.toLong() as Long? },
                reverseMapper = {
                    if (it == null) observable.value
                    else observable.value.toYear(Year(it.toInt()))
                }
        ))
    }


}