package com.lightningkite.koolui.test

import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.kommunicate.HttpClient
import com.lightningkite.kommunicate.HttpMethod
import com.lightningkite.kommunicate.callSerializer
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.test.models.Post
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.mirror.info.list
import com.lightningkite.mirror.info.type
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HttpCallTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Http Call Test"

    val data = observableListOf<Post>()

    init {
        //By Database
        GlobalScope.launch {
            try {
                val results = HttpClient.callSerializer(
                        url = "https://jsonplaceholder.typicode.com/posts",
                        method = HttpMethod.GET,
                        type = Post::class.type.list
                )
                println(results)
                data.replace(results)
            } catch(e:Exception){
                println(e.stackTraceString())
            }
        }
    }

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "This is the web test.", alignPair = AlignPair.CenterCenter)
            +work(
                    list(
                            data = data,
                            makeView = {
                                card(vertical {
                                    -text(text = it.transform { it.title }, size = TextSize.Subheader)
                                    -text(text = it.transform { it.body }, size = TextSize.Body)
                                })
                            }
                    ),
                    data.onListUpdate.transform { it.isEmpty() }
            )
        }.margin(8f)
    }
}
