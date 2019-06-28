package com.lightningkite.koolui.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.android.*
import com.lightningkite.koolui.android.access.AccessibleActivity
import com.lightningkite.koolui.builders.contentRoot
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.test.MainVG
import com.lightningkite.koolui.test.MyViewFactory
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.recktangle.Rectangle

class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<Layout<*, View>>()
    }

    class Factory(
            val activity: AccessibleActivity,
            colorSet: ColorSet = theme.main
    ) : MyViewFactory<Layout<*, View>>, ViewFactory<Layout<*, View>> by LayoutMaterialViewFactory(activity, theme, colorSet) {
        override fun withColorSet(colorSet: ColorSet): ViewFactory<Layout<*, View>> = Factory(activity, colorSet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationAccess.init(this, R.drawable.ic_notifications)

//        val rootLayout = Factory(this).contentRoot(OriginalTestVG())
        val rootLayout = Factory(this).contentRoot(main)
        setContentView(LayoutToAndroidView(this).apply { layout = rootLayout })
        println("Laid out")
//        rootLayout.layout(Rectangle(0f,0f,400f,400f))
//        setContentView(rootLayout.viewAdapter.viewAsBase)
    }


}
