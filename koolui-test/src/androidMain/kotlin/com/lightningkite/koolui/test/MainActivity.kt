package com.lightningkite.koolui.test

import android.os.Bundle
import android.view.View
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.android.*
import com.lightningkite.koolui.android.access.AccessibleActivity
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.view.MaterialAndroidViewFactory

class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<Layout<*, View>>()
    }

    class Factory(
            val activity: AccessibleActivity,
            colorSet: ColorSet = theme.main
    ) : MyViewFactory<Layout<*, View>>, ViewFactory<Layout<*, View>> by MaterialAndroidViewFactory(activity, myTheme, colorSet)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationAccess.init(this, R.drawable.ic_notifications)

//        val rootLayout = Factory(this).contentRoot(OriginalTestVG())
        val factory = Factory(this)
        val rootLayout = factory.contentRoot(main.generate(factory))
        setContentView(LayoutToAndroidView(this).apply { layout = rootLayout })
        println("Laid out")
//        rootLayout.layout(Rectangle(0f,0f,400f,400f))
//        setContentView(rootLayout.viewAdapter.viewAsBase)
    }


}
