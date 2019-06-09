package com.lightningkite.koolui.test

import com.lightningkite.koolui.Location
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.transform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GeolocationTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Geolocation Test"

    val data = StandardObservableProperty(Geohash(0))
    val address = StandardObservableProperty("")

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "Location:")
            -text(data.transform { "${it.latitude}, ${it.longitude}" })
            -button("Get My Location"){
                GlobalScope.launch(Dispatchers.UI) {
                    try {
                        data.value = Location.requestOnce("Get your location once to insert it into the form", 100.0).location
                    } catch(e:Exception){

                    }
                }
            }
            -button("Load address info") {
                GlobalScope.launch(Dispatchers.UI) {
                    Location.getAddress(data.value)?.let {
                        address.value = it
                    }
                }
            }
            -textField(address)
            -button("To Lat/Long") {
                GlobalScope.launch(Dispatchers.UI) {
                    Location.getGeohash(address.value)?.let {
                        data.value = it
                    }
                }
            }
        }
    }
}