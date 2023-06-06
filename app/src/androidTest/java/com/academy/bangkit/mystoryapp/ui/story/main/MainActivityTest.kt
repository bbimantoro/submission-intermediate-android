package com.academy.bangkit.mystoryapp.ui.story.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.ui.story.post.PostStoryActivity
import com.academy.bangkit.mystoryapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun takePicture_Success() {
        Intents.init()
        onView(withId(R.id.addStoryFab)).perform(click())

        intended(hasComponent(PostStoryActivity::class.java.name))
        onView(withId(R.id.cameraBtn)).perform(click())
        onView(withId(R.id.cameraActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.captureIv)).perform(click())
        onView(withId(R.id.postStoryActivity)).check(matches(isDisplayed()))
    }
}