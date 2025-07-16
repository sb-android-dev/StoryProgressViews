package com.sbdev.project.storyprogressviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes

class StoryProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressBarLayoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    private val spaceLayoutParams = LayoutParams(5, LayoutParams.WRAP_CONTENT)

    private val progressBars: ArrayList<PausableProgressBar> = ArrayList()

    private var storiesCount: Int = 0
    private var progressColor: Int = Color.WHITE
    private var backgroundColor: Int = Color.GRAY
    private var current: Int = -1
    private var storiesListener: StoriesListener? = null
    private var isComplete: Boolean = false

    private var isSkipStart: Boolean = false
    private var isReverseStart: Boolean = false

    init {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        context.withStyledAttributes(attrs, R.styleable.StoriesProgressView) {
            storiesCount = getInt(R.styleable.StoriesProgressView_progressCount, 0)
            progressColor = getColor(R.styleable.StoriesProgressView_progressColor, Color.WHITE)
            backgroundColor = getColor(R.styleable.StoriesProgressView_progressBackgroundColor, Color.GRAY)
        }
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

        for (i in 0 until storiesCount) {
            val p: PausableProgressBar = createProgressBar()
            progressBars.add(p)
            addView(p)
            if ((i + 1) < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        return PausableProgressBar(context).apply {
            layoutParams = progressBarLayoutParams
            setProgressColor(progressColor)
            setBackgroundColorCustom(backgroundColor)
        }
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = spaceLayoutParams
        return v
    }

    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    fun setStoriesListener(storiesListener: StoriesListener) {
        this.storiesListener = storiesListener
    }

    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return

        val p = progressBars[current]
        isSkipStart = true
        p.setMaxAndNotify()
    }

    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return

        val p = progressBars[current]
        isReverseStart = true
        p.setMinAndNotify()
    }

    fun setStoryDuration(duration: Long) {
        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(duration)
            progressBars[i].setProgressCallback(callback(i))
        }
    }

    fun setStoriesCountWithDuration(durations: LongArray) {
        storiesCount = durations.size
        bindViews()

        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(durations[i])
            progressBars[i].setProgressCallback(callback(i))
        }
    }

    private fun callback(index: Int): PausableProgressBar.ProgressCallback {
        return object : PausableProgressBar.ProgressCallback {
            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress() {
                if (isReverseStart) {
                    storiesListener?.onPrev()

                    if (0 <= (current - 1)) {
                        val p = progressBars[current - 1]
                        p.setMin()
                        progressBars[--current].startProgress()
                    } else {
                        progressBars[current].startProgress()
                    }
                    isReverseStart = false
                    return
                }

                val next = current + 1
                if (next <= (progressBars.size - 1)) {
                    storiesListener?.onNext()
                    progressBars[next].startProgress()
                } else {
                    isComplete = true
                    storiesListener?.onComplete()
                }
                isSkipStart = false
            }
        }
    }

    fun startStories() {
        progressBars[0].startProgress()
    }

    fun startStories(from: Int) {
        for (i in 0 until from) {
            progressBars[i].setMax()
        }

        progressBars[from].startProgress()
    }

    fun clear() {
        progressBars.forEach {
            it.setMin()
        }
    }

    fun pause() {
        if (current < 0) return
        progressBars[current].pauseProgress()
    }

    fun resume() {
        if (current < 0) return
        progressBars[current].resumeProgress()
    }

    interface StoriesListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }
}