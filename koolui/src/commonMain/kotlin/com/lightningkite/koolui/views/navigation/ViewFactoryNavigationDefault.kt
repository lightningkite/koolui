package com.lightningkite.koolui.views.navigation

import com.lightningkite.kommon.collection.pop
import com.lightningkite.kommon.collection.reset
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.image.withOptions
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.interactive.button
import com.lightningkite.koolui.views.interactive.imageButton
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.lastOrNullObservable
import com.lightningkite.reacktive.property.*
import com.lightningkite.recktangle.Point
import kotlin.math.ceil

interface ViewFactoryNavigationDefault<VIEW> :
        Themed,
        ViewFactoryNavigation<VIEW>,
        ViewFactoryBasic<VIEW>,
        ViewFactoryLayout<VIEW>,
        ViewFactoryInteractive<VIEW>{

    override fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>
    ) = vertical {
        -usingColorSet(theme.bar) {
            frame(horizontal {
                defaultAlign = Align.Center
                -imageButton(
                        imageWithOptions = ConstantObservableProperty(
                                MaterialIcon.arrowBack.color(theme.bar.foreground).withOptions(
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
                    usingColorSet(theme.bar) {
                        (it?.generateActions(dependency) ?: space(1f))
                    } to Animation.Fade
                })
            }).background(theme.bar.background)
        }
        +frame(swap(stack.lastOrNullObservableWithAnimations().transform {
            (it.first?.generate(dependency) ?: space(Point.zero)) to it.second
        })
        ).background(theme.main.background)

        if (!tabs.isEmpty()) {
            usingColorSet(theme.bar) {
                -frame(horizontal {
                    for (tab in tabs) {
                        +button(tab.first.text, tab.first.imageWithOptions) {
                            stack.reset(tab.second)
                        }
                    }
                }).background(theme.bar.background)
            }
        }
    }

    override fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, VIEW>
    ): VIEW = vertical {
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
                    imageWithOptions = ConstantObservableProperty(
                            MaterialIcon.chevronLeft.color(colorSet.foreground).withOptions(defaultSize = Point(24f, 24f))
                    ),
                    onClick = {
                        page.value = page.value.minus(1).coerceIn(pageGenerator.indices)
                    }
            )
            AlignPair.BottomCenter - text(
                    text = page.transform { "${it + 1} / ${pageGenerator.size}" },
                    size = TextSize.Tiny
            )
            AlignPair.BottomRight - imageButton(
                    imageWithOptions = ConstantObservableProperty(
                            MaterialIcon.chevronRight.color(colorSet.foreground).withOptions(defaultSize = Point(24f, 24f))
                    ),
                    onClick = {
                        page.value = page.value.plus(1).coerceIn(pageGenerator.indices)
                    }
            )
        }
    }

    override fun tabs(options: ObservableList<TabItem>, selected: MutableObservableProperty<TabItem>): VIEW = swap(options.onListUpdate.transform {
        horizontal {
            for (option in it) {
                val view = imageButton(option.imageWithOptions, option.text, Importance.Low) {
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

    val pageSize: Int get() = 20
    override fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int>,
            lastIndex: MutableObservableProperty<Int>,
            direction: Direction,
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
                            val emptyView = space()
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
                            val emptyView = space()
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
                        imageWithOptions = ConstantObservableProperty(
                                MaterialIcon.chevronLeft.color(colorSet.foreground).withOptions(
                                        defaultSize = Point(
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
                        imageWithOptions = ConstantObservableProperty(
                                MaterialIcon.chevronRight.color(colorSet.foreground).withOptions(
                                        defaultSize = Point(
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
}