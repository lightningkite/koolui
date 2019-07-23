package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.kommon.collection.pop
import com.lightningkite.kommon.collection.reset
import com.lightningkite.koolui.builders.*
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TabItem
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.withSizing
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.lastOrNullObservableWithAnimations
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.lokalize.time.Date
import com.lightningkite.lokalize.time.Month
import com.lightningkite.lokalize.time.Time
import com.lightningkite.lokalize.time.Year
import com.lightningkite.reacktive.list.*
import com.lightningkite.reacktive.property.*
import com.lightningkite.recktangle.Point
import kotlin.math.ceil

fun <VIEW> ViewFactory<VIEW>.defaultEntryContext(
        label: String,
        help: String?,
        icon: ImageWithSizing?,
        feedback: ObservableProperty<Pair<Importance, String>?>,
        field: VIEW
) = horizontal {

    defaultAlign = Align.Center

    if (icon != null) {
        -(image(ConstantObservableProperty(icon))).margin(2f)

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
}.margin(6f)

fun <VIEW, T> ViewFactory<VIEW>.defaultList(
        pageSize: Int = 100,
        buttonColor: Color,
        data: ObservableList<T>,
        direction: Direction,
        firstIndex: MutableObservableProperty<Int>,
        lastIndex: MutableObservableProperty<Int>,
        makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> VIEW
): VIEW {
    var setByUi = false
    val pageObs = StandardObservableProperty(firstIndex.value / pageSize)
    firstIndex += listen@{
        if (setByUi) {
            setByUi = false
            return@listen
        }
        pageObs.value = firstIndex.value / pageSize
    }
    lastIndex += listen@{
        if (setByUi) {
            setByUi = false
            return@listen
        }
        pageObs.value = lastIndex.value / pageSize
    }
    pageObs += {
        setByUi = true
        firstIndex.value = it * pageSize
        setByUi = true
        lastIndex.value = it * (pageSize + 1) - 1
    }
    return vertical {
        var previous = pageObs.value
        +swap(pageObs.transform { page ->
            val anim = when {
                page < previous -> Animation.Pop
                page > previous -> Animation.Push
                else -> Animation.Fade
            }
            previous = page
            val progression = if (direction.uiPositive) {
                0 until pageSize
            } else {
                pageSize downTo 0
            }
            val backup = data.firstOrNull()
            if (direction.vertical) {
                scrollVertical(vertical {
                    for (pageOffset in progression) {
                        val index = page * pageSize + pageOffset
                        val emptyView = space(24f)
                        var filledView: VIEW? = null
                        fun getFilledView(): VIEW {
                            if (filledView != null) return filledView!!
                            val obs: ObservableProperty<T> =
                                    data.onListUpdate.transform { it.getOrNull(index) ?: backup as T }
                            filledView = makeView(obs, ConstantObservableProperty(index))
                            return filledView as VIEW
                        }
                        -swap(data.onListUpdate.transform {
                            val view = if (index in it.indices)
                                getFilledView()
                            else
                                emptyView
                            view to Animation.None
                        })
                    }
                }) to anim
            } else {
                scrollHorizontal(horizontal {
                    for (pageOffset in progression) {
                        val index = page * pageSize + pageOffset
                        val emptyView = space(24f)
                        var filledView: VIEW? = null
                        fun getFilledView(): VIEW {
                            if (filledView != null) return filledView!!
                            val obs: ObservableProperty<T> =
                                    data.onListUpdate.transform { it.getOrNull(index) ?: backup as T }
                            filledView = makeView(obs, ConstantObservableProperty(index))
                            return filledView as VIEW
                        }
                        -swap(data.onListUpdate.transform {
                            val view = if (index in it.indices)
                                getFilledView()
                            else
                                emptyView
                            view to Animation.None
                        })
                    }
                }) to anim
            }
        })
        -align {
            AlignPair.BottomLeft - imageButton(
                    imageWithSizing = ConstantObservableProperty(
                            MaterialIcon.chevronLeft.color(buttonColor).withSizing(
                                    Point(
                                            24f,
                                            24f
                                    )
                            )
                    ),
                    onClick = {
                        val newPage = pageObs.value - 1
                        if (newPage >= 0 && newPage < data.size / pageSize.toDouble()) {
                            pageObs.value = newPage
                        }
                    }
            ).alpha(CombineObservableProperty2(pageObs, data.onListUpdate) { page, data ->
                val newPage = page - 1
                if (newPage >= 0 && newPage < data.size / pageSize.toDouble()) 1f else 0f
            })
            AlignPair.BottomCenter - text(
                    text = CombineObservableProperty2(
                            pageObs,
                            data.onListUpdate
                    ) { page, data -> "${page + 1} / ${ceil(data.size / pageSize.toDouble()).toInt()}" },
                    size = TextSize.Tiny
            )
            AlignPair.BottomRight - imageButton(
                    imageWithSizing = ConstantObservableProperty(
                            MaterialIcon.chevronRight.color(buttonColor).withSizing(
                                    Point(
                                            24f,
                                            24f
                                    )
                            )
                    ),
                    onClick = {
                        val newPage = pageObs.value + 1
                        if (newPage >= 0 && newPage < data.size / pageSize.toDouble()) {
                            pageObs.value = newPage
                        }
                    }
            ).alpha(CombineObservableProperty2(pageObs, data.onListUpdate) { page, data ->
                val newPage = page + 1
                if (newPage >= 0 && newPage < data.size / pageSize.toDouble()) 1f else 0f
            })
        }
    }
}

fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.defaultLargeWindow(
        theme: Theme,
        barBuilder: ViewFactory<VIEW>,
        dependency: DEPENDENCY,
        stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
        tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>
) = vertical {
    -with(barBuilder) {
        horizontal {
            defaultAlign = Align.Center
            -imageButton(
                    imageWithSizing = ConstantObservableProperty(
                            MaterialIcon.arrowBack.color(theme.bar.foreground).withSizing(
                                    Point(
                                            24f,
                                            24f
                                    )
                            )
                    ),
                    importance = Importance.Low,
                    onClick = { if (stack.size > 1) stack.pop() }
            ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })

            -text(text = stack.onListUpdate.transform { it.lastOrNull()?.title ?: "" }, size = TextSize.Header)

            +space(Point(5f, 5f))

            -swap(stack.lastOrNullObservable().transform {
                (it?.generateActions(dependency) ?: space(1f)) to Animation.Fade
            })
        }.background(theme.bar.background)
    }

    if (tabs.isEmpty()) {
        +swap(stack.lastOrNullObservableWithAnimations().transform {
            (it.first?.generate(dependency) ?: space(Point.zero)) to it.second
        })
                .background(theme.main.background)
    } else {
        +horizontal {
            -scrollVertical(vertical {
                for (tab in tabs) {
                    -button(tab.first.text, tab.first.imageWithSizing, importance = Importance.Low) {
                        stack.reset(tab.second)
                    }
                }
            }).background(theme.main.backgroundHighlighted)
            +swap(stack.lastOrNullObservableWithAnimations().transform {
                (it.first?.generate(dependency) ?: space(Point.zero)) to it.second
            })
                    .background(theme.main.background)

        }.background(theme.main.background)
    }
}

fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.defaultSmallWindow(
        theme: Theme,
        barBuilder: ViewFactory<VIEW>,
        dependency: DEPENDENCY,
        stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
        tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>
) = vertical {
    -with(barBuilder) {
        frame(horizontal {
            defaultAlign = Align.Center
            -imageButton(
                    imageWithSizing = ConstantObservableProperty(
                            MaterialIcon.arrowBack.color(theme.bar.foreground).withSizing(
                                    Point(
                                            24f,
                                            24f
                                    )
                            )
                    ),
                    importance = Importance.Low,
                    onClick = { if (stack.size > 1) stack.pop() }
            ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })

            -text(text = stack.onListUpdate.transform { it.lastOrNull()?.title ?: "" }, size = TextSize.Header)

            +space(Point(5f, 5f))

            -swap(stack.lastOrNullObservable().transform {
                (it?.generateActions(dependency) ?: space(1f)) to Animation.Fade
            })
        }).background(theme.bar.background)
    }

    +frame(swap(stack.lastOrNullObservableWithAnimations().transform {
        (it.first?.generate(dependency) ?: space(Point.zero)) to it.second
    })
    ).background(theme.main.background)

    if (!tabs.isEmpty()) {
        -frame(horizontal {
            for (tab in tabs) {
                +button(tab.first.text, tab.first.imageWithSizing) {
                    stack.reset(tab.second)
                }
            }
        }).background(theme.bar.background)
    }
}

fun <VIEW> ViewFactory<VIEW>.defaultDatePicker(observable: MutableObservableProperty<Date>) = horizontal {
    -integerField(observable.transform(
            mapper = { it.year.sinceAD.toLong() as Long? },
            reverseMapper = {
                if (it == null) observable.value
                else observable.value.toYear(Year(it.toInt()))
            }
    ))
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
                    mapper = { it.dayOfMonth },
                    reverseMapper = {
                        observable.value.toDayInMonth(it)
                    }
            )
    )
}

fun <VIEW> ViewFactory<VIEW>.defaultTimePicker(observable: MutableObservableProperty<Time>) = horizontal {
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

fun <VIEW> ViewFactory<VIEW>.defaultRefresh(view: VIEW, working: ObservableProperty<Boolean>, onRefresh: () -> Unit) = align {
    AlignPair.FillFill - view
    AlignPair.TopRight - work(imageButton(
            imageWithSizing = MaterialIcon.refresh.color(colorSet.foreground).withSizing(),
            onClick = onRefresh
    ), working)
}


fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.defaultPages(
        buttonColor: Color,
        dependency: DEPENDENCY,
        page: MutableObservableProperty<Int>,
        vararg pageGenerator: ViewGenerator<DEPENDENCY, VIEW>
) = vertical {
    var previous = page.value
    +swap(page.transform {
        val anim = when {
            page.value < previous -> Animation.Pop
            page.value > previous -> Animation.Push
            else -> Animation.Fade
        }
        previous = it
        pageGenerator[it.coerceIn(pageGenerator.indices)].generate(dependency) to anim
    })
    -align {
        AlignPair.BottomLeft - imageButton(
                imageWithSizing = ConstantObservableProperty(
                        MaterialIcon.chevronLeft.color(buttonColor).withSizing(
                                Point(24f, 24f)
                        )
                ), onClick = {
            page.value = page.value.minus(1).coerceIn(pageGenerator.indices)
        })
        AlignPair.BottomCenter - text(
                text = page.transform { "${it + 1} / ${pageGenerator.size}" },
                size = TextSize.Tiny
        )
        AlignPair.BottomRight - imageButton(
                imageWithSizing = ConstantObservableProperty(
                        MaterialIcon.chevronRight.color(
                                buttonColor
                        ).withSizing(Point(24f, 24f))
                ), onClick = {
            page.value = page.value.plus(1).coerceIn(pageGenerator.indices)
        })
    }
}


fun <VIEW> ViewFactory<VIEW>.defaultTabs(
        options: ObservableList<TabItem>,
        selected: MutableObservableProperty<TabItem>
) = swap(options.onListUpdate.transform {
    horizontal {
        for (option in it) {
            val view = imageButton(option.imageWithSizing, option.text, Importance.Low) {
                selected.value = option
            }.alpha(CombineObservableProperty2(selected, option.enabled) { selectedOption, isEnabled ->
                if (!isEnabled) .4f else if (option == selectedOption) 1f else 0.7f
            })
            if (it.size > 4) {
                -view
            } else {
                +view
            }
        }
    } to Animation.Fade
})

fun <VIEW> ViewFactory<VIEW>.defaultLaunchSelector(
        title: String? = null,
        options: List<Pair<String, () -> Unit>>
): Unit = launchDialog(true, {}) {
    card(scrollVertical(vertical {
        title?.let { title -> -text(text = title, size = TextSize.Header, align = AlignPair.CenterCenter) }

        for (option in options) {
            -button(
                    label = option.first,
                    onClick = option.second
            )
        }

    }))
}
