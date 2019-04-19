package com.lightningkite.koolui.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ViewSwitcher
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.android.AndroidMaterialViewFactory
import com.lightningkite.koolui.android.access.AccessibleActivity
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.test.MainVG
import com.lightningkite.koolui.test.MyViewFactory
import com.lightningkite.koolui.views.ViewFactory

class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<View>()
    }

    class Factory(
            activity: AccessibleActivity
    ) : MyViewFactory<View>, ViewFactory<View> by AndroidMaterialViewFactory(activity, Theme.dark()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApplicationAccess.init(this, R.drawable.ic_notifications)

        val factory = Factory(this)
        setContentView(factory.contentRoot(main.generate(factory)))
    }


}
