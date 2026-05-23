package com.ilyadev.meowmoments.presentation.ui.detail

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.di.RepositoryModule
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.testing.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, qualifiers = "w480dp-h800dp")
class FactDetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: CatFactsRepository = mock(CatFactsRepository::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    private fun launchWithArguments(fact: CatFact) {
        val bundle = Bundle().apply {
            putParcelable("fact", fact)
        }
        launchFragmentInHiltContainer<FactDetailFragment>(bundle)
    }

    @Test
    fun displays_fact_details_correctly() {
        val testFact = CatFact(
            id = 1L,
            text = "Test fact",
            category = "Test",
            imageUrl = null,
            dateReceived = "2026-05-18"
        )

        launchWithArguments(testFact)

        onView(withId(R.id.tv_fact_text)).perform(scrollTo())
        onView(withId(R.id.tv_fact_text)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(testFact.text)
                )
            )
        )
        onView(withId(R.id.tv_fact_category)).perform(scrollTo())
        onView(withId(R.id.tv_fact_category)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText("#${testFact.category}")
                )
            )
        )
    }

    @Test
    fun click_on_favorite_button_toggles_status() {
        val testFact = CatFact(
            id = 1L,
            text = "Test fact",
            category = "Test",
            imageUrl = null,
            dateReceived = "2026-05-18",
            isFavorite = false
        )

        launchWithArguments(testFact)

        onView(withId(R.id.btn_favorite)).perform(scrollTo(), click())
    }

    @Test
    fun click_on_share_button_opens_share_intent() {
        val testFact = CatFact(
            id = 1L,
            text = "Test fact",
            category = "Test",
            imageUrl = null,
            dateReceived = "2026-05-18"
        )

        launchWithArguments(testFact)

        onView(withId(R.id.btn_share)).perform(scrollTo(), click())
    }
}
