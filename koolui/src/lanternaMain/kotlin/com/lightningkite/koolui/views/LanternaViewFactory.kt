//@file:Suppress("unused", "CanBeParameter")
//
//package com.lightningkite.koolui.views
//
//import com.googlecode.lanterna.SGR
//import com.googlecode.lanterna.TextCharacter
//import com.googlecode.lanterna.TextColor
//import com.googlecode.lanterna.graphics.StyleSet
//import com.googlecode.lanterna.input.KeyStroke
//import com.googlecode.lanterna.input.KeyType
//import com.googlecode.lanterna.screen.Screen
//import com.googlecode.lanterna.terminal.Terminal
//import com.lightningkite.koolui.color.Color
//import com.lightningkite.koolui.color.ColorSet
//import com.lightningkite.koolui.color.Theme
//import com.lightningkite.koolui.concepts.*
//import com.lightningkite.koolui.geometry.AlignPair
//import com.lightningkite.koolui.geometry.Direction
//import com.lightningkite.koolui.geometry.LinearPlacement
//import com.lightningkite.koolui.image.imageWithOptions
//import com.lightningkite.koolui.views.ViewFactory
//import com.lightningkite.koolui.views.ViewGenerator
//import com.lightningkite.koolui.views.virtual.View
//import com.lightningkite.lokalize.time.Date
//import com.lightningkite.lokalize.time.DateTime
//import com.lightningkite.lokalize.time.Time
//import com.lightningkite.reacktive.list.MutableObservableList
//import com.lightningkite.reacktive.list.ObservableList
//import com.lightningkite.reacktive.list.lastOrNullObservable
//import com.lightningkite.reacktive.property.ConstantObservableProperty
//import com.lightningkite.reacktive.property.MutableObservableProperty
//import com.lightningkite.reacktive.property.ObservableProperty
//import com.lightningkite.reacktive.property.lifecycle.bind
//import com.lightningkite.reacktive.property.lifecycle.listen
//import com.lightningkite.recktangle.Point
//import com.lightningkite.recktangle.Rectangle
//import java.util.*
//import kotlin.math.roundToInt
//
//open class LanternaViewFactory(
//        override val theme: Theme = Theme(),
//        override val colorSet: ColorSet = theme.main
//) : ViewFactory<View> {
//    override fun withColorSet(colorSet: ColorSet): LanternaViewFactory = LanternaViewFactory(theme, colorSet)
//
//    override val View.lifecycle: ObservableProperty<Boolean> get() = this.attached
//
//
//}
//
//interface LanternaView {
//    val children: Sequence<LanternaView> get() = sequenceOf()
//
//    //rendering
//    fun render(screen: Screen, bounds: Rectangle, hasFocus: Boolean)
//
//    val interactive: Boolean get() = false
//    fun interaction(keyStroke: KeyStroke): Boolean { return false }
//}
//
//data class LanternaStyle(
//        val foreground: TextColor = TextColor.ANSI.DEFAULT,
//        val background: TextColor = TextColor.ANSI.DEFAULT,
//        val flags: List<SGR> = listOf()
//) {
//    val arrayOfFlags = flags.toTypedArray()
//    val enumSet = when(flags.size) {
//        0 -> EnumSet.noneOf(SGR::class.java)
//        1 -> EnumSet.of(flags.first())
//        else -> EnumSet.of(flags.first(), *flags.subList(1, flags.size).toTypedArray())
//    }
//    fun character(character: Char): TextCharacter = TextCharacter(character, foreground, background, *arrayOfFlags)
//}
//
//var StyleSet<*>.style: LanternaStyle
//    get() = LanternaStyle(foreground = this.foregroundColor, background = this.backgroundColor, flags = this.activeModifiers.toList())
//    set(value){
//        foregroundColor = value.foreground
//        backgroundColor = value.background
//        setModifiers(value.enumSet)
//    }
//
//class LanternaTextView : LanternaView {
//    var style = LanternaStyle()
//    var text = ""
//    var cursorLocation: Int = 0
//
//    override fun render(screen: Screen, bounds: Rectangle, hasFocus: Boolean) {
//        val displayText = if(hasFocus){
//            text.toMutableList().apply { add(cursorLocation.coerceIn(text.indices), '|') }.joinToString("")
//        } else {
//            cursorLocation = text.length
//            text
//        }
//        screen.newTextGraphics().let { graphics ->
//            graphics.style = style
//            graphics.putString(bounds.left.roundToInt(), bounds.top.roundToInt(), displayText)
//        }
//    }
//
//    override val interactive: Boolean get() = true
//
//    override fun interaction(keyStroke: KeyStroke): Boolean {
//        when(keyStroke.keyType) {
//             KeyType.Character -> {
//                text = text.toMutableList().apply {
//                    add(cursorLocation.coerceIn(text.indices), keyStroke.character)
//                }.joinToString("")
//                cursorLocation++
//            }
//            KeyType.ArrowLeft -> cursorLocation--
//            KeyType.ArrowRight -> cursorLocation++
//            KeyType.Backspace -> {
//                text = text.toMutableList().apply {
//                    removeAt(cursorLocation.coerceIn(text.indices))
//                }.joinToString("")
//                cursorLocation--
//            }
//            KeyType.Insert -> TODO()
//            KeyType.Delete -> {
//                text = text.toMutableList().apply {
//                    removeAt(cursorLocation.coerceIn(text.indices))
//                }.joinToString("")
//            }
//            KeyType.Home -> cursorLocation = 0
//            KeyType.End -> cursorLocation = text.length
//            KeyType.Enter -> TODO()
//            else -> return false
//        }
//        return true
//    }
//}