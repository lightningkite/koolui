package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.views.graphics.*
import com.lightningkite.koolui.image.Image
import com.lightningkite.koolui.image.withOptions
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.response.readBytes

class UrlImageTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "URL imageWithOptions Test"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "An imageWithOptions will be loaded here from 'https://picsum.photos/200/300'.")
            -loadingImage { Image.fromByteArray(HttpClient().call("https://picsum.photos/200/300").response.readBytes()).withOptions() }
        }
    }
}


