package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.recktangle.Rectangle
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
                set(left + x, top + y, value)
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

    class DummyAdapter(val name: String = "") : ViewAdapter {
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

        fun chars2D(): Chars2D = Chars2D(rect.width.toInt().plus(1), rect.height.toInt().plus(1), '.').apply {
            if (name.isNotEmpty()) {
                box(0, 0, rect.width.toInt(), rect.height.toInt())
                text(2, 0, rect.width.toInt().minus(3), name)
            }

            for (child in children) {
                blit(child.rect.left.toInt(), child.rect.top.toInt(), child.chars2D())
            }
        }
    }

    fun leaf(text: String) = Layout.leaf(DummyAdapter(text), 2f, text.length.toFloat() + 3f, 3f)
    fun vertical(
            text: String = "",
            children: List<Pair<LinearPlacement, Layout>>
    ): Layout {
        val viewAdapter = DummyAdapter(text)
        return Layout.vertical(viewAdapter, children.also { it.forEach { viewAdapter.children.add(it.second.viewAdapter as DummyAdapter) } })
    }

    fun horizontal(
            text: String = "",
            children: List<Pair<LinearPlacement, Layout>>
    ): Layout {
        val viewAdapter = DummyAdapter(text)
        return Layout.horizontal(viewAdapter, children.also { it.forEach { viewAdapter.children.add(it.second.viewAdapter as DummyAdapter) } })
    }

    fun align(
            text: String = "",
            children: List<Pair<AlignPair, Layout>>
    ): Layout {
        val viewAdapter = DummyAdapter(text)
        return Layout.align(viewAdapter, children.also { it.forEach { viewAdapter.children.add(it.second.viewAdapter as DummyAdapter) } })
    }

    fun frame(
            text: String = "",
            child: Layout,
            leftMargin: Float = 0f,
            rightMargin: Float = 0f,
            topMargin: Float = 0f,
            bottomMargin: Float = 0f
    ): Layout {
        val viewAdapter = DummyAdapter(text)
        return Layout.frame(viewAdapter, child.also { viewAdapter.children.add(it.viewAdapter as DummyAdapter) }, leftMargin, rightMargin, topMargin, bottomMargin)
    }

    @Test
    fun test() {
        val layout = frame("frame",
                horizontal("", listOf(
                        LinearPlacement.fillFill to vertical("", listOf(
                                LinearPlacement.wrapStart to leaf("wrapStart"),
                                LinearPlacement.wrapCenter to leaf("wrapCenter"),
                                LinearPlacement.wrapEnd to leaf("wrapEnd"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.wrapStart to leaf("wrapStart"),
                                LinearPlacement.wrapCenter to leaf("wrapCenter"),
                                LinearPlacement.wrapEnd to leaf("wrapEnd")
                        )),
                        LinearPlacement.wrapFill to vertical("", listOf(
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.wrapStart to leaf("wrapStart"),
                                LinearPlacement.wrapCenter to leaf("wrapCenter"),
                                LinearPlacement.wrapEnd to leaf("wrapEnd"),
                                LinearPlacement.fillFill to leaf("fillFill")
                        )),
                        LinearPlacement.fillFill to vertical("", listOf(
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill"),
                                LinearPlacement.fillFill to leaf("fillFill")
                        ))
                ))
        )

        layout.layout(Rectangle(left = 0f, right = 80f, top = 0f, bottom = 60f))
        println(buildString { (layout.viewAdapter as DummyAdapter).chars2D().print(this) })
    }
}