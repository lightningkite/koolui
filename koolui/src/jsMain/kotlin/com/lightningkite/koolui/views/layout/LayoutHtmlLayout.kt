package com.lightningkite.koolui.views.layout

import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.layout.views.LayoutVFLayout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.makeElement
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface LayoutHtmlLayout : LayoutVFLayout<HTMLElement>, LayoutViewWrapper<HTMLElement> {
    override fun scrollVertical(
            view: Layout<*, HTMLElement>,
            amount: MutableObservableProperty<Float>
    ): Layout<*, HTMLElement> = Layout(
            viewAdapter = makeElement<HTMLDivElement>("div").adapter(),
            x = PassOnDimensionLayout(view.x),
            y = ScrollDimensionLayout(view.y)
    ).apply {
        with(this.viewAsBase){
            style.overflowY = "auto"
            var suppressListener = false
            isAttached.bind(amount) {
                if (suppressListener) {
                    return@bind Unit
                }
                suppressListener = true
                scrollLeft = it.toDouble()
                suppressListener = false
            }

            onscroll = listener@{
                if (suppressListener) {
                    return@listener Unit
                }
                suppressListener = true
                amount.value = scrollTop.toFloat()
                suppressListener = false

                Unit
            }
        }
    }

    override fun scrollHorizontal(
            view: Layout<*, HTMLElement>,
            amount: MutableObservableProperty<Float>
    ): Layout<*, HTMLElement> = Layout(
            viewAdapter = makeElement<HTMLDivElement>("div").adapter(),
            x = ScrollDimensionLayout(view.x),
            y = PassOnDimensionLayout(view.y)
    ).apply {
        with(this.viewAsBase){
            style.overflowX = "auto"
            var suppressListener = false
            isAttached.bind(amount) {
                if (suppressListener) {
                    return@bind Unit
                }
                suppressListener = true
                scrollLeft = it.toDouble()
                suppressListener = false
            }

            onscroll = listener@{
                if (suppressListener) {
                    return@listener Unit
                }
                suppressListener = true
                amount.value = scrollLeft.toFloat()
                suppressListener = false

                Unit
            }
        }
    }

    override fun scrollBoth(
            view: Layout<*, HTMLElement>,
            amountX: MutableObservableProperty<Float>,
            amountY: MutableObservableProperty<Float>
    ): Layout<*, HTMLElement> = Layout(
            viewAdapter = makeElement<HTMLDivElement>("div").adapter(),
            x = ScrollDimensionLayout(view.x),
            y = PassOnDimensionLayout(view.y)
    ).apply {
        with(this.viewAsBase){
            var suppressListener = false

            style.overflowX = "auto"
            isAttached.bind(amountX) {
                if (suppressListener) {
                    return@bind Unit
                }
                suppressListener = true
                scrollLeft = it.toDouble()
                suppressListener = false
            }

            style.overflowY = "auto"
            isAttached.bind(amountY) {
                if (suppressListener) {
                    return@bind Unit
                }
                suppressListener = true
                scrollLeft = it.toDouble()
                suppressListener = false
            }

            onscroll = listener@{
                if (!suppressListener) {
                    suppressListener = true
                    amountX.value = scrollLeft.toFloat()
                    amountY.value = scrollTop.toFloat()
                    suppressListener = false
                }

                Unit
            }
        }
    }
}