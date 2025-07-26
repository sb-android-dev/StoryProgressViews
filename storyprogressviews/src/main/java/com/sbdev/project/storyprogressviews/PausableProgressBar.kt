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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var progressBackgroundColor = Color.GRAY
    private var progressColor = Color.WHITE

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
            }

            if(progress < 1f) {
                postDelayed(this, 16)
            } else {
                callback?.onFinishProgress()
            }
        }
    }

    /**
     * Starts the progress animation from the beginning.
     * Resets progress, sets start time, unpauses, notifies callback, and starts animation.
     */
    fun startProgress() {
        progress = 0f
        startTime = System.currentTimeMillis()
        isPaused = false
        callback?.onStartProgress()
        post(updateRunnable)
    }

    /**
     * Sets the color of the progress bar.
     *
     * @param color The color to set for the progress bar.
     */
    fun setProgressColor(color: Int) {
        this.progressColor = color
        invalidate()
    }

    /**
     * Sets the background color of the progress bar.
     *
     * @param color The color to set as the background.
     */
    fun setBackgroundColorCustom(color: Int) {
        this.progressBackgroundColor = color
        invalidate()
    }

    /**
     * Sets the duration of the progress animation.
     *
     * @param duration The desired duration in milliseconds.
     */
    fun setDuration(duration: Long) {
        this.duration = duration
    }

    /**
     * Sets a callback to be invoked when the progress starts or finishes.
     *
     * @param callback The callback to be set.
     */
    fun setProgressCallback(callback: ProgressCallback) {
        this.callback = callback
    }

    /** Resumes paused progress, adjusting start time and restarting the animation.
     *  Does nothing if already running.
     */
    fun resumeProgress() {
        if(!isPaused) return

        isPaused = false
        startTime = System.currentTimeMillis() - elapsedTimeAtPause
        post(updateRunnable)
    }

    /** Pauses the progress bar animation, records the elapsed time, and stops further updates.
     *  Does nothing if already paused.
     */
    fun pauseProgress() {
        if(isPaused) return

        isPaused = true
        elapsedTimeAtPause = System.currentTimeMillis() - startTime
        removeCallbacks(updateRunnable)
    }

    /**
     * Sets the progress to its maximum value (1f) and redraws the view.
     * Optionally notifies the callback that the progress has finished.
     *
     * @param isNotify If true, the `onFinishProgress` callback will be invoked. Defaults to false.
     */
    fun fillProgress(isNotify: Boolean = false) {
        removeCallbacks(updateRunnable)
        progress = 1f
        invalidate()

        if(isNotify)
            callback?.onFinishProgress()
    }

    /**
     * Clears the progress to 0 and redraws the view.
     * Optionally notifies the callback that progress has finished.
     *
     * @param isNotify If true, invokes [ProgressCallback.onFinishProgress]. Defaults to false.
     */
    fun clearProgress(isNotify: Boolean = false) {
        removeCallbacks(updateRunnable)
        progress = 0f
        invalidate()

        if(isNotify)
            callback?.onFinishProgress()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val heightCenter = height / 2f

        // Draw Background
        paint.color = progressBackgroundColor
        canvas.drawRect(0f, heightCenter - 5f, width.toFloat(), heightCenter + 5f, paint)

        // Draw Progress
        paint.color = progressColor
        canvas.drawRect(0f, heightCenter - 5f, width * progress, heightCenter + 5f, paint)
    }

    interface ProgressCallback {
        fun onStartProgress()
        fun onFinishProgress()
    }
}