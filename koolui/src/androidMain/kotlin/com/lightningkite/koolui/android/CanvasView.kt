package com.lightningkite.koolui.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.view.View
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class CanvasView(context: Context) : View(context) {

    companion object {
        val renderingThreads = ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                1,
                TimeUnit.SECONDS,
                LinkedBlockingQueue<Runnable>()
        )
    }

    var back: Bitmap? = null
    var front: Bitmap? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        startRender()
    }

    val canvas = Canvas()
    val stage = AtomicInteger(0)
    var render: Canvas.() -> Unit = {}
        set(value) {
            field = value
            startRender()
        }

    var renderRequested = false
    fun startRender() {
        renderRequested = true
        if(width == 0 || height == 0) return
        if (stage.compareAndSet(0, 1)) {
            renderRequested = false
            renderingThreads.execute {
                val existingBack = back
                val bitmap = if(existingBack != null && existingBack.width == width && existingBack.height == height){
                    existingBack
                } else {
                    existingBack?.recycle()
                    val updated = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    back = updated
                    updated
                }
                back = null
                canvas.setBitmap(bitmap)
                canvas.drawColor(0x0, PorterDuff.Mode.CLEAR)
                canvas.render()
                canvas.setBitmap(null)
                post {
                    back = front
                    front = bitmap
                    stage.compareAndSet(1, 2)
                    invalidate()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val front = front ?: return
        canvas.drawBitmap(front, 0f, 0f, null)
        stage.compareAndSet(2, 0)
        if(renderRequested){
            startRender()
        }
    }
}