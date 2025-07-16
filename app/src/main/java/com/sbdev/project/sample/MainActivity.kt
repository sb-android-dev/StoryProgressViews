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

        setupStoriesProgressView()
    }

    private fun setupStoriesProgressView() {
        val storiesProgressView = findViewById<StoryProgressView>(R.id.storyProgress)
        storiesProgressView.setStoriesCount(5)
        storiesProgressView.setStoryDuration(5_000L)
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
                Log.d(MainActivity::class.java.simpleName, "onNext is called")
            }

            override fun onPrev() {
                Log.d(MainActivity::class.java.simpleName, "onPrev is called")
            }

            override fun onComplete() {
                storiesProgressView.clear()
                storiesProgressView.startStories()
            }
        })
    }
}