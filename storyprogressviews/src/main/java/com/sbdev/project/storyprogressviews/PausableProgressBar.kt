package com.sbdev.project.storyprogressviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PausableProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var progress = 0f
    private var duration = 2_000L
    private var startTime = 0L
    private var isPaused = false
    private var elapsedTimeAtPause = 0L

    private var callback: ProgressCallback? = null

    private val updateRunnable = object : Runnable {
        override fun run() {
            if(!isPaused) {
                val elapsed = System.currentTimeMillis() - startTime
                progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
                invalidate()

                if(progress < 1f) {
                    postDelayed(this, 16)
                } else {
                    callback?.onFinishProgress()
                }
            }
        }
    }

    fun setProgressCallback(callback: ProgressCallback) {
        this.callback = callback
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun startProgress() {
        progress = 0f
        startTime = System.currentTimeMillis()
        isPaused = false
        callback?.onStartProgress()
        post(updateRunnable)
    }

    fun pauseProgress() {
        if(!isPaused) {
            isPaused = true
            elapsedTimeAtPause = System.currentTimeMillis() - startTime
            removeCallbacks(updateRunnable)
        }
    }

    fun resumeProgress() {
        if(isPaused) {
            isPaused = false
            startTime = System.currentTimeMillis() - elapsedTimeAtPause
            post(updateRunnable)
        }
    }

    fun setMaxAndNotify() {
        progress = 1f
        invalidate()
        callback?.onFinishProgress()
    }

    fun setMinAndNotify() {
        progress = 0f
        invalidate()
        callback?.onFinishProgress()
    }

    fun setMax() {
        progress = 1f
        invalidate()
    }

    fun setMin() {
        progress = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val heightCenter = height / 2f

        // Draw Background
        canvas.drawRect(0f, heightCenter - 5f, width.toFloat(), heightCenter + 5f, paintBackground)

        // Draw Progress
        canvas.drawRect(0f, heightCenter - 5f, width * progress, heightCenter + 5f, paintProgress)
    }

    interface ProgressCallback {
        fun onStartProgress()
        fun onFinishProgress()
    }
}