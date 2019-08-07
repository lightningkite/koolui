package com.lightningkite.koolui.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.view.View
import com.lightningkite.koolui.async.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class CanvasView(context: Context): View(context){

    var back: Bitmap? = null
    var front: Bitmap? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (front?.width != w || front?.height != h) {
            front?.recycle()
            back?.recycle()
            front = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            back = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            startRender()
        }
    }

    val canvas = Canvas()
    val stage = AtomicInteger(0)
    var render: Canvas.()->Unit = {}
        set(value){
            field = value
            startRender()
        }

    fun startRender() {
        invalidate()
        val bitmap = back ?: return
        if (stage.compareAndSet(0, 1)) {
            GlobalScope.launch(Dispatchers.Default) {
                canvas.setBitmap(bitmap)
                canvas.drawColor(0x0, PorterDuff.Mode.CLEAR)
                canvas.render()
                canvas.setBitmap(null)
                launch(Dispatchers.UI){
                    val swap = front
                    front = back
                    back = swap
                    stage.compareAndSet(1, 2)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val front = front ?: return
        canvas.drawBitmap(front, 0f, 0f, null)
        stage.compareAndSet(2, 0)
    }
}