[![](https://jitpack.io/v/sb-android-dev/StoryProgressViews.svg)](https://jitpack.io/#sb-android-dev/StoryProgressViews)

# StoryProgressViews

A customizable Android UI library for displaying story-like progress indicators (similar to Instagram/Facebook stories) with pause and resume support.

## 📦 Features

- Story-style progress view with multiple segments
- Progress bar that can be paused and resumed
- Customizable progress and background colors
- Easy integration in any Android project

## 🛠️ Installation

Add this library to your project by including it as a module or publishing to your own Maven repository.

If you're adding as a module:

1. Add the library module to your `settings.gradle.kts`:

   ```kotlin
   include(":storyprogressviews")
   ```
2. Add the dependency in your `app/build.gradle.kts`:

    ```kotlin
    implementation(project(":storyprogressviews"))
    ```

## 🧱 Usage
Here's an example of how to use `StoryProgressView` in your layout and code:

### XML
   ```xml
   <com.sbdev.project.storyprogressviews.StoryProgressView
     android:id="@+id/storyProgressView"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     app:progressCount="5"
     app:progressColor="#FF5722"
     app:backgroundColor="#BDBDBD" />
   ```

### Kotlin
   ```kotlin
   storyProgressView.startProgress()

   // To pause
   storyProgressView.pause()

   // To resume
   storyProgressView.resume()

   // To skip to next
   storyProgressView.skip()

   // To go back
   storyProgressView.reverse()
   ```

## ⚙️ Customization
You can customize the following attributes via XML:
- `app:progressCount`: Number of progress segments
- `app:progressColor`: Color of the active progress
- `app:backgroundColor`: Color of the inactive/background progress

