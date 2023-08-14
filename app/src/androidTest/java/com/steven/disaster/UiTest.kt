package com.steven.disaster

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.steven.disaster.view.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UiTest {
    @get: Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun lightDarkMode() {
        onView(withId(R.id.menu_settings)).perform(click())
        onView(withId(R.id.tv_dark_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_enable_dark_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.switch_dark_mode)).check(matches(isDisplayed()))
            .perform(click())
        onView(isRoot()).perform(pressBack())
    }

    @Test
    fun map() {
        onView(withId(R.id.map_fragment)).check(matches(isDisplayed()))
            .perform(swipeRight())
            .perform(swipeDown())
            .perform(swipeLeft())
            .perform(swipeUp())
    }

    @Test
    fun bottomSheet() {
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOnChip() {
        onView(withId(R.id.chip_group_disaster)).check(matches(isDisplayed()))
        onView(withId(R.id.chip_flood)).check(matches(isDisplayed()))
            .perform(click())
    }

    @Test
    fun search() {
        onView(withId(R.id.search_bar_location)).check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.search_view_location)).check(matches(isDisplayed()))

        onView(isAssignableFrom(EditText::class.java))
            .perform(typeText("aceh"))
            .perform(pressImeActionButton())
    }
}