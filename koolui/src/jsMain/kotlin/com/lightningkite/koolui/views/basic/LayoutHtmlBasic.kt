package com.lightningkite.koolui.views.basic

import com.lightningkite.kommon.asInt8Array
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.ImageScaleType
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.image.withOptions
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.makeElement
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.toCssClass
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import kotlin.dom.addClass

interface LayoutHtmlBasic : ViewFactoryBasic<Layout<*, HTMLElement>>, LayoutViewWrapper<HTMLElement>, Themed {
    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ): Layout<*, HTMLElement> {
        return when (size) {
            TextSize.Tiny -> intrinsicLayout(makeElement<HTMLParagraphElement>("p") {
                addClass("TinyText")
                this.align = when (align.horizontal) {
                    Align.Start -> "left"
                    Align.Center -> "center"
                    Align.End -> "right"
                    Align.Fill -> "justify"
                }
            }) { layout ->
                layout.isAttached.bind(text) {
                    this.textContent = it
                }
            }
            TextSize.Body -> intrinsicLayout(makeElement<HTMLParagraphElement>("p") {
                addClass("TinyText")
                this.align = when (align.horizontal) {
                    Align.Start -> "left"
                    Align.Center -> "center"
                    Align.End -> "right"
                    Align.Fill -> "justify"
                }
            }) { layout ->
                layout.isAttached.bind(text) {
                    this.textContent = it
                }
            }
            TextSize.Subheader -> intrinsicLayout(makeElement<HTMLParagraphElement>("h4") {
                addClass("TinyText")
                this.align = when (align.horizontal) {
                    Align.Start -> "left"
                    Align.Center -> "center"
                    Align.End -> "right"
                    Align.Fill -> "justify"
                }
            }) { layout ->
                layout.isAttached.bind(text) {
                    this.textContent = it
                }
            }
            TextSize.Header -> intrinsicLayout(makeElement<HTMLParagraphElement>("h1") {
                addClass("TinyText")
                this.align = when (align.horizontal) {
                    Align.Start -> "left"
                    Align.Center -> "center"
                    Align.End -> "right"
                    Align.Fill -> "justify"
                }
            }) { layout ->
                layout.isAttached.bind(text) {
                    this.textContent = it
                }
            }
        }.apply {
            with(viewAsBase) {
                addClass(importance.toCssClass())
                style.verticalAlign = when (align.vertical) {
                    Align.Start -> "top"
                    Align.Center -> "middle"
                    Align.End -> "bottom"
                    Align.Fill -> "middle"
                }
                style.textOverflow = "ellipsis"
                if (maxLines != Int.MAX_VALUE) {
                    val em = style.lineHeight.removeSuffix("em").toDoubleOrNull() ?: 1.2
                    style.lineHeight = "${em}em"
                    style.maxHeight = "${em * maxLines}em"
                }
            }
        }
    }

    override fun image(imageWithOptions: ObservableProperty<ImageWithOptions>): Layout<*, HTMLElement> = intrinsicLayout(makeElement<HTMLImageElement>("img")) { layout ->
        layout.isAttached.bind(imageWithOptions) {
            it.image.url?.let { url ->
                src = url
            } ?: it.image.data?.let { data ->
                val url = URL.createObjectURL(Blob(arrayOf(data.asInt8Array().buffer)))
                src = url
                onload?.invoke(Event("")) //Stops memory leaks when switching images rapidly
                onload = {
                    URL.revokeObjectURL(url)
                }
            }
            style.objectFit = when (it.scaleType) {
                ImageScaleType.Crop -> "cover"
                ImageScaleType.Fill -> "scale-down"
                ImageScaleType.Center -> "none"
            }
            it.defaultSize?.let { pt ->
                style.width = pt.x.toString() + "px"
                style.height = pt.y.toString() + "px"
            }
        }
    }

    override fun work(): Layout<*, HTMLElement> = image(
            Image.fromSvgString(
                    //<!-- By Sam Herbert (@sherb), for everyone. More @ http://goo.gl/7AJzbL -->
                    """
<svg width="38" height="38" viewBox="0 0 38 38" xmlns="http://www.w3.org/2000/svg" stroke="${colorSet.foreground.toWeb()}">
    <g fill="none" fill-rule="evenodd">
        <g transform="translate(1 1)" stroke-width="2">
            <circle stroke-opacity=".5" cx="18" cy="18" r="18"/>
            <path d="M36 18c0-9.94-8.06-18-18-18">
                <animateTransform
                    attributeName="transform"
                    type="rotate"
                    from="0 18 18"
                    to="360 18 18"
                    dur="1s"
                    repeatCount="indefinite"/>
            </path>
        </g>
    </g>
</svg>"""
            ).withOptions(defaultSize = Point(24f, 24f))
    )

    override fun Layout<*, HTMLElement>.background(color: ObservableProperty<Color>): Layout<*, HTMLElement> {
        isAttached.bind(color) {
            viewAsBase.style.backgroundColor = it.toWeb()
        }
        return this
    }

    override fun Layout<*, HTMLElement>.background(color: Color): Layout<*, HTMLElement> {
        viewAsBase.style.backgroundColor = color.toWeb()
        return this
    }

    override fun Layout<*, HTMLElement>.alpha(alpha: ObservableProperty<Float>): Layout<*, HTMLElement> {
        isAttached.bind(alpha) {
            viewAsBase.style.opacity = it.toString()
        }
        return this
    }

    override fun Layout<*, HTMLElement>.alpha(alpha: Float): Layout<*, HTMLElement> {
        viewAsBase.style.opacity = alpha.toString()
        return this
    }
}