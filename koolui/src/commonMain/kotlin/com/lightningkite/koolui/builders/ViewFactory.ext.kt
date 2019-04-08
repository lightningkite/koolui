package com.lightningkite.koolui.builders

import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point

fun <VIEW> ViewFactory<VIEW>.text(
        size: TextSize = TextSize.Body,
        alignPair: AlignPair = AlignPair.CenterLeft,
        importance: Importance = Importance.Normal,
        maxLines: Int = Int.MAX_VALUE,
        text: String
): VIEW = text(
        text = ConstantObservableProperty(text),
        size = size,
        importance = importance,
        align = alignPair,
        maxLines = maxLines
)

fun <VIEW> ViewFactory<VIEW>.image(
        imageWithSizing: ImageWithSizing
): VIEW = image(ConstantObservableProperty(imageWithSizing))

fun <VIEW> ViewFactory<VIEW>.space(size: Float): VIEW = space(Point(size, size))
fun <VIEW> ViewFactory<VIEW>.space(width: Float, height: Float): VIEW = space(Point(width, height))

fun <VIEW> ViewFactory<VIEW>.button(
        label: String,
        imageWithSizing: ImageWithSizing? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = button(
        label = ConstantObservableProperty(label),
        imageWithSizing = ConstantObservableProperty(imageWithSizing),
        importance = importance,
        onClick = onClick
)

fun <VIEW> ViewFactory<VIEW>.imageButton(
        imageWithSizing: ImageWithSizing,
        label: String? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = imageButton(
        label = ConstantObservableProperty(label),
        imageWithSizing = ConstantObservableProperty(imageWithSizing),
        importance = importance,
        onClick = onClick
)

fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.pagesEmbedded(
        dependency: DEPENDENCY,
        page: MutableObservableProperty<Int>,
        vararg pageGenerators: (DEPENDENCY) -> VIEW
) = pages(dependency, page, *pageGenerators.map {
    object : ViewGenerator<DEPENDENCY, VIEW> {
        @Suppress("UNCHECKED_CAST")
        override fun generate(dependency: DEPENDENCY): VIEW = (dependency as ViewFactory<VIEW>).space(5f)
    }
}.toTypedArray())

fun <VIEW> ViewFactory<VIEW>.loadingImage(
        imageWithSizingObservable: ObservableProperty<ImageWithSizing?>
) = work(image(imageWithSizing = imageWithSizingObservable.transform { it ?: ImageWithSizing.none }), imageWithSizingObservable.transform { it == null })

fun <VIEW> ViewFactory<VIEW>.launchConfirmationDialog(
        title: String = "",
        message: String = "Are you sure you want to do this?",
        onCancel: () -> Unit = {},
        onConfirm: () -> Unit
) {
    var approved = false
    launchDialog(
            dismissable = true,
            onDismiss = {
                if (approved)
                    onConfirm.invoke()
                else
                    onCancel.invoke()
            }
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "Cancel") {
                    approved = false
                    dismiss()
                }
                -button(label = "OK") {
                    approved = true
                    dismiss()
                }
            }
        }))
    }
}

fun <VIEW> ViewFactory<VIEW>.launchInfoDialog(
        title: String,
        message: String,
        onDismiss: () -> Unit = {}
) {
    launchDialog(
            dismissable = true,
            onDismiss = onDismiss
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "OK") {
                    dismiss()
                }
            }
        }))
    }
}
