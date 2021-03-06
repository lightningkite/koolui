package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.*
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.navigation.*
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.views.layout.space

class SelectorVG<VIEW>(
        val stack: MutableObservableList<MyViewGenerator<VIEW>>
) : MyViewGenerator<VIEW> {
    override val title: String = "KotlinX UI Test"

    val tests = observableListOf<Pair<String, () -> MyViewGenerator<VIEW>>>(
            "Canvas" to { CanvasTestVG<VIEW>() },
            "GeolocationTest" to { GeolocationTestVG<VIEW>() },
            "OpenUriTest" to { OpenUriTestVG<VIEW>() },
            "FilesTest" to { FilesTestVG<VIEW>() },
            "UrlImageTest" to { UrlImageTestVG<VIEW>() },
            "Space Test" to { SpaceTestVG<VIEW>() },
            "Original Test" to { OriginalTestVG<VIEW>() },
            "Alpha" to { AlphaTestVG<VIEW>() },
            "Horizontal" to { HorizontalVG<VIEW>() },
            "Vertical" to { VerticalTestVG<VIEW>() },
            "Pages" to { PagesVG<VIEW>() },
            "Frame" to { FrameVG<VIEW>() },
            "Controls" to { ControlsVG<VIEW>() },
            "Notifications" to { NotificationTestVG<VIEW>() },
            "Icons" to { IconsTestVG<VIEW>() },
            "Dialog" to { DialogTestVG<VIEW>() },
            "PentagameTestVG" to { PentagameTestVG<VIEW>() },
            "SwapTestVG" to { SwapTestVG<VIEW>() }
    )

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        list(data = tests, makeView = { itemObs ->
            button(
                    label = itemObs.transform { item -> item.first },
                    importance = Importance.Low,
                    onClick = {
                        stack.push(itemObs.value.second.invoke())
                    }
            )
        }).margin(8f).clickable {
            space(2f)
        }
    }
}
