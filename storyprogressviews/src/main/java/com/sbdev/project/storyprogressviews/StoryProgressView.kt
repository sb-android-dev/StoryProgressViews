package com.sbdev.project.storyprogressviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.sbdev.project.storyprogressviews.Constants.BACKGROUND_COLOR
import com.sbdev.project.storyprogressviews.Constants.PROGRESS_COLOR
import com.sbdev.project.storyprogressviews.Constants.PROGRESS_DURATION
import com.sbdev.project.storyprogressviews.Constants.PROGRESS_SPACE
import com.sbdev.project.storyprogressviews.Constants.STORIES_COUNT

class StoryProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val progressBars: ArrayList<PausableProgressBar> = ArrayList()

    private val progressBarLayoutParams by lazy {
        LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
    }
    private val spaceLayoutParams by lazy {
        LayoutParams(0, LayoutParams.WRAP_CONTENT)
    }

    // Properties
    private var storiesCount: Int = 0
    private var progressColor: Int = 0
    private var backgroundColor: Int = 0
    private var progressDuration: Long = 0L
    private var progressSpace: Float = 0f

    private var current: Int = -1
    private var storiesListener: StoriesListener? = null
    private var isComplete: Boolean = false
    private var isReverseStart: Boolean = false

    init {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        orientation = HORIZONTAL
        context.withStyledAttributes(attrs, R.styleable.StoriesProgressView) {
            storiesCount = getInt(R.styleable.StoriesProgressView_progressCount, STORIES_COUNT)
            progressColor = getColor(R.styleable.StoriesProgressView_progressColor, PROGRESS_COLOR)
            backgroundColor = getColor(R.styleable.StoriesProgressView_progressBackgroundColor, BACKGROUND_COLOR)
            progressDuration = getInt(R.styleable.StoriesProgressView_progressDuration, PROGRESS_DURATION).toLong()
            progressSpace = getDimension(R.styleable.StoriesProgressView_progressSpace, PROGRESS_SPACE)
        }
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

        repeat(storiesCount) { index ->
            val progressBar = createProgressBar()
            progressBars.add(progressBar)
            addView(progressBar)
            if (index < storiesCount - 1) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        return PausableProgressBar(context).apply {
            layoutParams = progressBarLayoutParams
            setProgressColor(progressColor)
            setBackgroundColorCustom(backgroundColor)
            setDuration(progressDuration)
        }
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = spaceLayoutParams.apply { width = progressSpace.toInt() }
        return v
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
                        p.clearProgress()
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
            }
        }
    }

    /**
     * Sets the number of stories and updates the progress bars accordingly.
     *
     * @param storiesCount The new number of stories.
     */
    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    /**
     * Sets the listener for story events.
     *
     * @param storiesListener The listener to be notified of story events.
     */
    fun setStoriesListener(storiesListener: StoriesListener) {
        this.storiesListener = storiesListener
    }

    /**
     * Assigns progress callbacks to each progress bar.
     * Each callback handles events like progress start and finish.
     */
    fun setCallbacks() {
        for (i in 0 until progressBars.size) {
            progressBars[i].setProgressCallback(callback(i))
        }
    }

    /**
     * Sets the duration for each story.
     *
     * @param duration Duration for each story. Defaults to [progressDuration] if 0L.
     * @param setCallback Whether to set the progress callback for each progress bar. Defaults to true.
     */
    fun setStoryDuration(duration: Long = 0L, setCallback: Boolean = true) {
        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(duration.takeUnless { it == 0L } ?: progressDuration)
            if(setCallback) {
                progressBars[i].setProgressCallback(callback(i))
            }
        }
    }

    /**
     * Sets the number of stories and their individual durations.
     * Updates story count, re-binds views, and sets durations for each progress bar.
     * Optionally sets progress callbacks.
     *
     * @param durations Durations for each story (in milliseconds). Array size determines story count.
     * @param setCallback If true, sets progress callbacks for each progress bar (default: true).
     */
    fun setStoriesCountWithDuration(durations: LongArray, setCallback: Boolean = true) {
        storiesCount = durations.size
        bindViews()

        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(durations[i])
            if(setCallback) {
                progressBars[i].setProgressCallback(callback(i))
            }
        }
    }

    /**
     * Starts playing stories from the beginning.
     */
    fun startStories() {
        progressBars[0].startProgress()
    }

    /**
     * Starts story playback from a specified index.
     * Fills progress for stories before 'from', then starts 'from'.
     *
     * @param from The index to start stories from.
     */
    fun startStories(from: Int) {
        for (i in 0 until from) {
            progressBars[i].fillProgress()
        }

        progressBars[from].startProgress()
    }

    /**
     * Clears the progress of all stories.
     * Resets the current story index to -1 and sets the completion status to false.
     */
    fun clear() {
        for (bar in progressBars) {
            bar.clearProgress()
        }

        current = -1
        isComplete = false
    }

    /**
     * Resumes the progress of the current story.
     * If there is no current story (current < 0), this function does nothing.
     */
    fun resume() {
        if (current < 0) return

        progressBars[current].resumeProgress()
    }

    /**
     * Pauses the current story progress.
     * If there is no current story (current < 0), the function does nothing.
     */
    fun pause() {
        if (current < 0) return

        progressBars[current].pauseProgress()
    }

    /**
     * Skips to the next story by filling the current story's progress bar.
     */
    fun skip() {
        progressBars[current].fillProgress(true)
    }

    /**
     * Moves to previous story by clearing the current story's progress bar.
     */
    fun reverse() {
        isReverseStart = true
        progressBars[current].clearProgress(true)
    }

    interface StoriesListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }
}