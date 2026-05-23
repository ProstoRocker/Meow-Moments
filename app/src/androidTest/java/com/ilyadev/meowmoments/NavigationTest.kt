package com.ilyadev.meowmoments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilyadev.meowmoments.presentation.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun click_on_main_tab_opens_main_fragment() {
        onView(withId(R.id.mainFragment)).perform(click())

        // Verify MainFragment is displayed
    }

    @Test
    fun click_on_calendar_tab_opens_calendar_fragment() {
        onView(withId(R.id.calendarFragment)).perform(click())

        // Verify CalendarFragment is displayed
    }

    @Test
    fun click_on_my_facts_tab_opens_my_facts_fragment() {
        onView(withId(R.id.myFactsFragment)).perform(click())

        // Verify MyFactsFragment is displayed
    }

    @Test
    fun click_on_settings_tab_opens_settings_fragment() {
        onView(withId(R.id.settingsFragment)).perform(click())

        // Verify SettingsFragment is displayed
    }
}
