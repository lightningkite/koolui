package com.lightningkite.koolui.android.access


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * An interface for accessing activities in a decentralized way, where multiple listeners can listen
 * to event like [onPause], [onResume], etc, and most importantly, can use [prepareOnResult] to set
 * up lambdas to occur when a result code is received.
 *
 * Created by joseph on 6/9/17.
 */
interface ActivityAccess {
    val activity: Activity?
    val context: Context

    val onResume: MutableCollection<() -> Unit>
    val onPause: MutableCollection<() -> Unit>
    val onSaveInstanceState: MutableCollection<(outState: Bundle) -> Unit>
    val onLowMemory: MutableCollection<() -> Unit>
    val onDestroy: MutableCollection<() -> Unit>
    val onActivityResult: MutableCollection<(request: Int, result: Int, data: Intent?) -> Unit>
    val onNewIntent: MutableCollection<(Intent) -> Unit>

    /**
     * When the back button is pressed, the lambdas in the list are invoked in reverse order until
     * one of the lambdas returns true, indicating that the button press has been handled.
     *
     * It is suggested that one add a lambda to the top of the list upon creation of a view that
     * needs its back button handled, and then to remove that lambda when the view goes away.
     */
    val onBackPressed: MutableList<() -> Boolean>

    fun prepareOnResult(
        presetCode: Int = (Math.random() * Short.MAX_VALUE).toInt(),
        onResult: (Int, Intent?) -> Unit = { a, b -> }
    ): Int

    fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit)
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit)

}