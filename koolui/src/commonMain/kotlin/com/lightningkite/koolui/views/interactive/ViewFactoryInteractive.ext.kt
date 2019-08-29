package com.lightningkite.koolui.views.interactive

import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.basic.work
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun <VIEW> ViewFactoryInteractive<VIEW>.button(
        label: String,
        imageWithOptions: ImageWithOptions? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = button(
        label = ConstantObservableProperty(label),
        imageWithOptions = ConstantObservableProperty(imageWithOptions),
        importance = importance,
        onClick = onClick
)

fun <VIEW> ViewFactoryInteractive<VIEW>.imageButton(
        imageWithOptions: ImageWithOptions,
        label: String? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = imageButton(
        imageWithOptions = ConstantObservableProperty(imageWithOptions),
        label = ConstantObservableProperty(label),
        importance = importance,
        onClick = onClick
)

fun <X, VIEW> X.suspendButton(
        label: String,
        imageWithOptions: ImageWithOptions? = null,
        importance: Importance = Importance.Normal,
        onClick: suspend () -> Unit
): VIEW where X : ViewFactoryBasic<VIEW>, X : ViewFactoryInteractive<VIEW>, X : ViewFactoryLayout<VIEW> {
    val working = StandardObservableProperty(false)
    return work(button(
            label = ConstantObservableProperty(label),
            imageWithOptions = ConstantObservableProperty(imageWithOptions),
            importance = importance,
            onClick = {
                GlobalScope.launch(Dispatchers.Main) {
                    working.value = true
                    onClick()
                    working.value = false
                }
            }
    ), working)
}

fun <X, VIEW> X.suspendImageButton(
        imageWithOptions: ImageWithOptions,
        label: String? = null,
        importance: Importance = Importance.Normal,
        onClick: suspend () -> Unit
): VIEW where X : ViewFactoryBasic<VIEW>, X : ViewFactoryInteractive<VIEW>, X : ViewFactoryLayout<VIEW> {
    val working = StandardObservableProperty(false)
    return work(imageButton(
            imageWithOptions = ConstantObservableProperty(imageWithOptions),
            label = ConstantObservableProperty(label),
            importance = importance,
            onClick = {
                GlobalScope.launch(Dispatchers.Main) {
                    working.value = true
                    onClick()
                    working.value = false
                }
            }
    ), working)
}

fun <VIEW> ViewFactoryInteractive<VIEW>.suspendRefresh(
        view: VIEW,
        working: MutableObservableProperty<Boolean> = StandardObservableProperty(false),
        refresh: suspend () -> Unit
) = refresh(view, working) {
    GlobalScope.launch(Dispatchers.Main) {
        working.value = true
        refresh()
        working.value = false
    }
}