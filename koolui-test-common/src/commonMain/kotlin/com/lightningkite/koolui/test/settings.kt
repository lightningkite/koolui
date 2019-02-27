package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

interface MyViewFactory<VIEW> : ViewFactory<VIEW> {}
typealias MyViewGenerator<VIEW> = ViewGenerator<MyViewFactory<VIEW>, VIEW>
