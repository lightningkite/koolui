package com.lightningkite.koolui.test

import com.lightningkite.kommunicate.HttpClient
import com.lightningkite.kommunicate.HttpMethod
import com.lightningkite.recktangle.Point
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.loadingImage
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.asImage
import com.lightningkite.koolui.image.callImage
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class UrlImageTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "URL Image Test"

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "An image will be loaded here from 'https://picsum.photos/200/300'.")
            //TODO
//            -loadingImage(suspend { HttpClient.callImage("https://picsum.photos/200/300", HttpMethod.GET).asImage() }
//                    .invokeObservable())
        }
    }
}
