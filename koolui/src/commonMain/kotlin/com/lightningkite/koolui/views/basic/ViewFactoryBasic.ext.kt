package com.lightningkite.koolui.views.basic

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.views.graphics.ViewFactoryGraphics
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun <VIEW> ViewFactoryBasic<VIEW>.text(
        text: String,
        size: TextSize = TextSize.Body,
        align: AlignPair = AlignPair.CenterLeft,
        importance: Importance = Importance.Normal,
        maxLines: Int = Int.MAX_VALUE
): VIEW = text(
        text = ConstantObservableProperty(text),
        size = size,
        importance = importance,
        align = align,
        maxLines = maxLines
)


fun <X, VIEW> X.work(view: VIEW, isWorking: ObservableProperty<Boolean>): VIEW where X: ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW> {
    val work = work()
    return swap(
            view = isWorking.transform {
                (if (it) work else view) to Animation.Fade
            },
            staticViewForSizing = view
    )
}

fun <X, VIEW> X.progress(view: VIEW, progress: ObservableProperty<Float>): VIEW where X: ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW> {
    val prog = progress(progress)
    return swap(
            view = progress.transform {
                (if (it == 1f) view else prog) to Animation.Fade
            },
            staticViewForSizing = view
    )
}
fun <VIEW> ViewFactoryBasic<VIEW>.image(
        imageWithOptions: ImageWithOptions
): VIEW = image(ConstantObservableProperty(imageWithOptions))

fun <X, VIEW> X.loadingImage(
        imageWithOptionsObservable: ObservableProperty<ImageWithOptions?>
): VIEW where X : ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW> {
    return work(image(imageWithOptions = imageWithOptionsObservable.transform {
        it ?: ImageWithOptions.none
    }), imageWithOptionsObservable.transform { it == null })
}

fun <X, VIEW> X.loadingImage(
        load: suspend () -> ImageWithOptions
): VIEW where X : ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW> {
    val obs = StandardObservableProperty<ImageWithOptions?>(null)
    return loadingImage(obs).apply {
        scope.launch(Dispatchers.UI) {
            obs.value = load()
        }
    }
}

fun <X, VIEW, T> X.loadingImage(
        observable: ObservableProperty<T>,
        load: suspend (T) -> ImageWithOptions
): VIEW where X : ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW> {
    val obs = StandardObservableProperty<ImageWithOptions?>(null)
    return loadingImage(obs).apply {
        lifecycle.bind(observable) {
            obs.value = null
            scope.launch(Dispatchers.UI) {
                obs.value = load(it)
            }
        }
    }
}