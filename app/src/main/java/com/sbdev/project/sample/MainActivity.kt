package com.sbdev.project.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sbdev.project.storyprogressviews.StoryProgressView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSimpleStoriesProgressView()
        setupStoriesProgressViewWithCustomProperties()
        setupStoriesProgressViewWithActions()
        setupStoriesProgressViewWithDifferentDurations()
    }

    private fun setupSimpleStoriesProgressView() {
        val storiesProgressView = findViewById<StoryProgressView>(R.id.simple_story_progress)
        storiesProgressView.setCallbacks()
        storiesProgressView.startStories()

        storiesProgressView.setStoriesListener(object : StoryProgressView.StoriesListener {
            override fun onNext() {
                // Perform action when goes to next story
            }

            override fun onPrev() {
                // Perform action when goes to previous story
            }

            override fun onComplete() {
                storiesProgressView.clear()
                storiesProgressView.startStories()
            }
        })
    }

    private fun setupStoriesProgressViewWithCustomProperties() {
        val storiesProgressView = findViewById<StoryProgressView>(R.id.story_progress_with_custom_properties)
        storiesProgressView.setStoriesCount(5)
        storiesProgressView.setStoryDuration(5_000L)
        storiesProgressView.startStories(1)

        storiesProgressView.setStoriesListener(object : StoryProgressView.StoriesListener {
            override fun onNext() {
                // Perform action when goes to next story
            }

            override fun onPrev() {
                // Perform action when goes to previous story
            }

            override fun onComplete() {
                storiesProgressView.clear()
                storiesProgressView.startStories(1)
            }
        })
    }

    private fun setupStoriesProgressViewWithActions() {
        val storiesProgressView = findViewById<StoryProgressView>(R.id.story_progress_with_actions)
        storiesProgressView.setCallbacks()
        storiesProgressView.startStories()

        val previousStory = findViewById<Button>(R.id.btn_previous)
        previousStory.setOnClickListener { storiesProgressView.reverse() }

        val nextStory = findViewById<Button>(R.id.btn_next)
        nextStory.setOnClickListener { storiesProgressView.skip() }

        val resumeStory = findViewById<Button>(R.id.btn_resume)
        resumeStory.setOnClickListener { storiesProgressView.resume() }

        val pauseStory = findViewById<Button>(R.id.btn_pause)
        pauseStory.setOnClickListener { storiesProgressView.pause() }

        storiesProgressView.setStoriesListener(object : StoryProgressView.StoriesListener {
            override fun onNext() {
                // Perform action when goes to next story

                val index = storiesProgressView.getCurrentStoryIndex()
                Log.d(MainActivity::class.java.simpleName, "onNext: current index: $index")
            }

            override fun onPrev() {
                // Perform action when goes to previous story

                val index = storiesProgressView.getCurrentStoryIndex()
                Log.d(MainActivity::class.java.simpleName, "onPrev: current index: $index")
            }

            override fun onComplete() {
                storiesProgressView.clear()
                storiesProgressView.startStories()
            }
        })
    }

    private fun setupStoriesProgressViewWithDifferentDurations() {
        val storiesProgressView = findViewById<StoryProgressView>(R.id.story_progress_with_different_durations)
        val storiesDuration = longArrayOf(5_000, 10_000, 15_000, 20_000)
        storiesProgressView.setStoriesCountWithDuration(storiesDuration)
        storiesProgressView.startStories()

        storiesProgressView.setStoriesListener(object : StoryProgressView.StoriesListener {
            override fun onNext() {
                // Perform action when goes to next story
            }

            override fun onPrev() {
                // Perform action when goes to previous story
            }

            override fun onComplete() {
                storiesProgressView.clear()
                storiesProgressView.startStories()
            }
        })
    }
}