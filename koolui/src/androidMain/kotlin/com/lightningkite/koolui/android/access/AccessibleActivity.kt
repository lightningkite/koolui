package com.lightningkite.koolui.android.access


import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * An activity that implements [ActivityAccess].
 *
 * Created by jivie on 10/12/15.
 */
abstract class AccessibleActivity : AppCompatActivity(), ActivityAccess {

    override val activity: Activity?
        get() = this
    override val context: Context
        get() = this

    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    override val onResume = HashSet<() -> Unit>()
    override fun onResume() {
        super.onResume()
        onResume.forEach { it.invoke() }
    }

    override val onPause = HashSet<() -> Unit>()
    override fun onPause() {
        onPause.forEach { it.invoke() }
        super.onPause()
    }

    override val onSaveInstanceState = HashSet<(outState: Bundle) -> Unit>()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceState.forEach { it.invoke(outState) }
    }

    override val onLowMemory = HashSet<() -> Unit>()
    override fun onLowMemory() {
        super.onLowMemory()
        onLowMemory.forEach { it.invoke() }
    }

    override val onBackPressed = ArrayList<() -> Boolean>()
    override fun onBackPressed() {
        if (!onBackPressed.reversed().any { it.invoke() }) {
            super.onBackPressed()
        }
    }

    override val onDestroy = HashSet<() -> Unit>()
    override fun onDestroy() {
        onDestroy.forEach { it.invoke() }
        super.onDestroy()
    }

    override val onNewIntent = HashSet<(Intent) -> Unit>()
    override fun onNewIntent(intent: Intent) {
        onNewIntent.forEach { it.invoke(intent) }
        super.onNewIntent(intent)
    }

    val requestReturns: HashMap<Int, (Map<String, Int>) -> Unit> = HashMap()

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override val onActivityResult = ArrayList<(Int, Int, Intent?) -> Unit>()

    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int {
        returns[presetCode] = onResult
        return presetCode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onActivityResult.forEach { it.invoke(requestCode, resultCode, data) }
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    /**
     * Requests a bunch of permissions and returns a map of permissions that were previously ungranted and their new status.
     */
    override fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit) {
        val ungranted = permission.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            val generated: Int = (Math.random() * 0xFFFF).toInt()

            requestReturns[generated] = onResult

            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), generated)

        } else {
            onResult(emptyMap())
        }
    }

    /**
     * Requests a single permissions and returns whether it was granted or not.
     */
    override fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            val generated: Int = (Math.random() * 0xFFFF).toInt()
            requestReturns[generated] = {
                onResult(it[permission] == PackageManager.PERMISSION_GRANTED)
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), generated)

        } else {
            onResult(true)
        }
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val map = HashMap<String, Int>()
            for (i in permissions.indices) {
                map[permissions[i]] = grantResults[i]
            }
            requestReturns[requestCode]?.invoke(map)

            requestReturns.remove(requestCode)
        }
    }
}