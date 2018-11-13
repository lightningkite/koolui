package desktop.kotlin.lk.test_android

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.lightningkite.kommunicate.HttpClient
import com.lightningkite.koolui.android.AndroidMaterialViewFactory
import com.lightningkite.koolui.android.access.AccessibleActivity
import com.lightningkite.koolui.android.lifecycle
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.configureUi
import com.lightningkite.koolui.test.MainVG

class MainActivity : AccessibleActivity() {

    companion object {
        val main = MainVG<View>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureUi(this, R.drawable.ic_notifications)

        val factory = AndroidMaterialViewFactory(this, Theme.dark())
        val view = factory.contentRoot(main.generate(factory))
        view.lifecycle.alwaysOn = true
        setContentView(view)
    }


}
