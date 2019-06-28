package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Rectangle
import kotlin.math.roundToInt
import kotlin.test.Test

class LayoutsTest {

    class Chars2D(val width: Int, val height: Int, val default: Char = ' ') {
        val array = CharArray(width * height) { default }
        inline operator fun get(x: Int, y: Int): Char {
            if (x >= width) return default
            if (y >= height) return default
            return array[y * width + x]
        }

        inline operator fun set(x: Int, y: Int, value: Char) {
            if (x >= width) return
            if (y >= height) return
            array[y * width + x] = value
        }

        inline fun positionX(index: Int): Int = index % width
        inline fun positionY(index: Int): Int = index / width

        inline fun forEach(action: (x: Int, y: Int, value: Char) -> Unit) {
            for (index in array.indices) {
                action(positionX(index), positionY(index), array[index])
            }
        }

        fun blit(left: Int, top: Int, other: Chars2D) {
            other.forEach { x, y, value ->
                if (value != default) {
                    set(left + x, top + y, value)
                }
            }
        }

        fun box(left: Int, top: Int, right: Int, bottom: Int) {
            for (x in left + 1..right - 1) {
                this[x, top] = '-'
                this[x, bottom] = '-'
            }
            for (y in top + 1..bottom - 1) {
                this[left, y] = '|'
                this[right, y] = '|'
            }
            this[left, top] = '+'
            this[right, top] = '+'
            this[left, bottom] = '+'
            this[right, bottom] = '+'
        }

        fun text(x: Int, y: Int, maxSize: Int, string: String) {
            if (maxSize <= 0) return
            string.take(maxSize).forEachIndexed { index, char ->
                this[x + index, y] = char
            }
        }

        fun print(out: Appendable) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    out.append(this[x, y])
                }
                out.append('\n')
            }
        }
    }

    class DummyAdapter(val name: String = "") : ViewAdapter<Unit, Unit> {
        override val view: Unit
            get() = Unit
        val rect = Rectangle()
        val children = ArrayList<DummyAdapter>()

        override fun updatePlacementX(start: Float, end: Float) {
            rect.left = start
            rect.right = end
        }

        override fun updatePlacementY(start: Float, end: Float) {
            rect.top = start
            rect.bottom = end
        }

        fun chars2D(): Chars2D = Chars2D(rect.width.roundToInt().plus(1), rect.height.roundToInt().plus(1), '.').apply {
            if (name.isNotEmpty()) {
                box(0, 0, rect.width.roundToInt(), rect.height.roundToInt())
                text(2, 0, rect.width.roundToInt().minus(3), name)
            }

            for (child in children) {
                blit(child.rect.left.toInt(), child.rect.top.toInt(), child.chars2D())
            }
        }

        override fun onAddChild(layout: Layout<*, Unit>) {
            children.add(layout.viewAdapter as DummyAdapter)
        }

        override fun onRemoveChild(layout: Layout<*, Unit>) {
            children.remove(layout.viewAdapter as DummyAdapter)
        }
    }

    fun Layout.Companion.leaf(dummyAdapter: DummyAdapter) = Layout(
            viewAdapter = dummyAdapter,
            x = LeafDimensionLayout {
                it.startMargin = 2f
                it.endMargin = 2f
                it.size = dummyAdapter.name.length.toFloat() + 3f
            },
            y = LeafDimensionLayout {
                it.startMargin = 2f
                it.endMargin = 2f
                it.size = 3f
            }
    )

    @Test
    fun test() {
        val layout = Layout.frame(DummyAdapter("test"),
                Layout.horizontal(DummyAdapter(""), listOf(
                        LinearPlacement.fillFill to Layout.vertical(DummyAdapter(""), listOf(
                                LinearPlacement.wrapStart to Layout.leaf(DummyAdapter("wrapStart")),
                                LinearPlacement.wrapCenter to Layout.leaf(DummyAdapter("wrapCenter")),
                                LinearPlacement.wrapEnd to Layout.leaf(DummyAdapter("wrapEnd")),
                                LinearPlacement.fillFill to Layout.leaf(DummyAdapter("fillFill")),
                                LinearPlacement.wrapStart to Layout.leaf(DummyAdapter("wrapStart")),
                                LinearPlacement.wrapCenter to Layout.leaf(DummyAdapter("wrapCenter")),
                                LinearPlacement.wrapEnd to Layout.leaf(DummyAdapter("wrapEnd"))
                        )),
                        LinearPlacement.wrapFill to Layout.vertical(DummyAdapter(""), listOf(
                                LinearPlacement.fillFill to Layout.leaf(DummyAdapter("fillFill")),
                                LinearPlacement.wrapStart to Layout.leaf(DummyAdapter("wrapStart")),
                                LinearPlacement.wrapCenter to Layout.leaf(DummyAdapter("wrapCenter")),
                                LinearPlacement.wrapEnd to Layout.leaf(DummyAdapter("wrapEnd")),
                                LinearPlacement.fillFill to Layout.leaf(DummyAdapter("fillFill"))
                        )),
                        LinearPlacement.fillFill to Layout.align(DummyAdapter(""), listOf(
                                AlignPair.TopLeft to Layout.leaf(DummyAdapter("TopLeft")),
                                AlignPair.TopCenter to Layout.leaf(DummyAdapter("TopCenter")),
                                AlignPair.TopRight to Layout.leaf(DummyAdapter("TopRight")),
                                AlignPair.CenterLeft to Layout.leaf(DummyAdapter("CenterLeft")),
                                AlignPair.CenterCenter to Layout.leaf(DummyAdapter("CenterCenter")),
                                AlignPair.CenterRight to Layout.leaf(DummyAdapter("CenterRight")),
                                AlignPair.BottomLeft to Layout.leaf(DummyAdapter("BottomLeft")),
                                AlignPair.BottomCenter to Layout.leaf(DummyAdapter("BottomCenter")),
                                AlignPair.BottomRight to Layout.leaf(DummyAdapter("BottomRightl"))
                        ))
                ))
        )

        layout.layout(Rectangle(left = 0f, right = 120f, top = 0f, bottom = 60f))
        println(buildString { (layout.viewAdapter as DummyAdapter).chars2D().print(this) })
    }

    @Test
    fun testDumbFrame() {
        val layout = Layout.frame(DummyAdapter("testDumbFrame"),
                Layout.horizontal(DummyAdapter(""), listOf(
                        LinearPlacement.wrapFill to Layout.frame(DummyAdapter(), Layout.leaf(DummyAdapter("asdf"))),
                        LinearPlacement.fillFill to Layout.frame(DummyAdapter(), Layout.leaf(DummyAdapter("asdf"))),
                        LinearPlacement.wrapFill to Layout.frame(DummyAdapter(), Layout.leaf(DummyAdapter("asdf")))
                ))
        )

        layout.layout(Rectangle(left = 0f, right = 120f, top = 0f, bottom = 60f))
        println(buildString { (layout.viewAdapter as DummyAdapter).chars2D().print(this) })
    }

    @Test
    fun testRelayout() {
        val obsA = StandardObservableProperty<Layout<*, Unit>>(Layout.leaf(DummyAdapter("Initial Value A")))
        val obsB = StandardObservableProperty<Layout<*, Unit>>(Layout.leaf(DummyAdapter("Initial Value B")))
        val obsC = StandardObservableProperty<Layout<*, Unit>>(Layout.leaf(DummyAdapter("Initial Value C")))
        val layout = Layout.frame(DummyAdapter("testRelayout"),
                Layout.horizontal(DummyAdapter(""), listOf(
                        LinearPlacement.wrapFill to Layout.swap(DummyAdapter(), obsA),
                        LinearPlacement.fillFill to Layout.swap(DummyAdapter(), obsB),
                        LinearPlacement.wrapFill to Layout.swap(DummyAdapter(), obsC),
                        LinearPlacement.wrapFill to Layout.leaf(DummyAdapter("Non-changing"))
                ))
        )
        layout.isAttached.alwaysOn = true
        layout.layout(Rectangle(left = 0f, right = 120f, top = 0f, bottom = 60f))
        println(buildString { (layout.viewAdapter as DummyAdapter).chars2D().print(this) })
        obsA.value = Layout.leaf(DummyAdapter("New Value"))
        obsB.value = Layout.leaf(DummyAdapter("New Value"))
        obsC.value = Layout.leaf(DummyAdapter("New Value"))
        layout.refresh()
        println(buildString { (layout.viewAdapter as DummyAdapter).chars2D().print(this) })
    }
}
