package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.loadingImage
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.withSizing
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.get
import io.ktor.client.response.readBytes

class UrlImageTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "URL ImageWithSizing Test"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "An imageWithSizing will be loaded here from 'https://picsum.photos/200/300'.")
            -loadingImage { Image.fromByteArray(HttpClient().call("https://picsum.photos/200/300").response.readBytes()).withSizing() }
        }
    }
}


