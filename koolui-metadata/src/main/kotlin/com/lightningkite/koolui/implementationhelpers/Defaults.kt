package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.kommon.collection.pop
import com.lightningkite.kommon.collection.reset
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.*
import com.lightningkite.koolui.builders.*
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.asImage
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.lastOrNullObservableWithAnimations
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.recktangle.Point
import kotlin.math.ceil

fun <VIEW> ViewFactory<VIEW>.defaultEntryContext(
    label: String,
    help: String?,
    icon: Image?,
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
                val v = if (it == null) space(Point(12f, 12f))
                else text(ConstantObservableProperty(it.second), importance = it.first, size = TextSize.Tiny)
                v to Animation.Fade
            }
        ).margin(2f)
    }
}.margin(6f)

fun <VIEW, T> ViewFactory<VIEW>.defaultList(
    pageSize: Int = 100,
    buttonColor: Color,
    data: ObservableList<T>,
    direction: Direction,
    firstIndex: MutableObservableProperty<Int>,
    lastIndex: MutableObservableProperty<Int>,
    makeView: (obs: ObservableProperty<T>) -> VIEW
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
                            filledView = makeView(obs)
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
                            filledView = makeView(obs)
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
        -frame {
            AlignPair.BottomLeft - imageButton(
                image = ConstantObservableProperty(
                    MaterialIcon.chevronLeft.color(buttonColor).asImage(
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
                image = ConstantObservableProperty(
                    MaterialIcon.chevronRight.color(buttonColor).asImage(
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
    tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>,
    actions: ObservableList<Pair<TabItem, () -> Unit>>
) = vertical {
    -with(barBuilder) {
        horizontal {
            defaultAlign = Align.Center
            -imageButton(
                image = ConstantObservableProperty(
                    MaterialIcon.arrowBack.color(theme.bar.foreground).asImage(
                        Point(
                            24f,
                            24f
                        )
                    )
                ),
                importance = Importance.Low,
                onClick = { if(stack.size > 1) stack.pop() }
            ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })

            -text(text = stack.onListUpdate.transform { it.lastOrNull()?.title ?: "" }, size = TextSize.Header)

            +space(Point(5f, 5f))

            -swap(actions.onListUpdate.transform {
                horizontal {
                    defaultAlign = Align.Center
                    for (item in it) {
                        -button(item.first.text, item.first.image) { item.second.invoke() }
                    }
                } to Animation.Fade
            })
        }.background(theme.bar.background)
    }

    if (tabs.isEmpty()) {
        +swap(stack.lastOrNullObservableWithAnimations().transform { (it.first?.generate(dependency) ?: space(Point.Zero)) to it.second })
                .background(theme.main.background)
    } else {
        +horizontal {
            -scrollVertical(vertical {
                for (tab in tabs) {
                    -button(tab.first.text, tab.first.image) {
                        stack.reset(tab.second)
                    }
                }
            }.background(theme.main.backgroundHighlighted))
            +swap(stack.lastOrNullObservableWithAnimations().transform { (it.first?.generate(dependency) ?: space(Point.Zero)) to it.second })
                    .background(theme.main.background)

        }.background(theme.main.background)
    }
}

fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.defaultSmallWindow(
        theme: Theme,
        barBuilder: ViewFactory<VIEW>,
        dependency: DEPENDENCY,
        stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
        tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>,
        actions: ObservableList<Pair<TabItem, () -> Unit>>
) = vertical {
    -with(barBuilder) {
        horizontal {
            defaultAlign = Align.Center
            -imageButton(
                image = ConstantObservableProperty(
                    MaterialIcon.arrowBack.color(theme.bar.foreground).asImage(
                        Point(
                            24f,
                            24f
                        )
                    )
                ),
                importance = Importance.Low,
                onClick = { if(stack.size > 1) stack.pop() }
            ).alpha(stack.onListUpdate.transform { if (it.size > 1) 1f else 0f })

            -text(text = stack.onListUpdate.transform { it.lastOrNull()?.title ?: "" }, size = TextSize.Header)

            +space(Point(5f, 5f))

            -swap(actions.onListUpdate.transform {
                horizontal {
                    defaultAlign = Align.Center
                    for (item in it) {
                        -button(item.first.text, item.first.image) { item.second.invoke() }
                    }
                } to Animation.Fade
            })
        }.background(theme.bar.background)
    }

    +swap(stack.lastOrNullObservableWithAnimations().transform { (it.first?.generate(dependency) ?: space(Point.Zero)) to it.second })
        .background(theme.main.background)

    if (!tabs.isEmpty()) {
        -horizontal {
            for (tab in tabs) {
                +button(tab.first.text, tab.first.image) {
                    stack.reset(tab.second)
                }
            }
        }.background(theme.bar.background)
    }
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
    -frame {
        AlignPair.BottomLeft - imageButton(
            image = ConstantObservableProperty(
                MaterialIcon.chevronLeft.color(buttonColor).asImage(
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
            image = ConstantObservableProperty(
                MaterialIcon.chevronRight.color(
                    buttonColor
                ).asImage(Point(24f, 24f))
            ), onClick = {
                page.value = page.value.plus(1).coerceIn(pageGenerator.indices)
            })
    }
}
