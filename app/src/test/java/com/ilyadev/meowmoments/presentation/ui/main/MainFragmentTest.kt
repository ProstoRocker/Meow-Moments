package com.ilyadev.meowmoments.presentation.ui.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.di.RepositoryModule
import com.ilyadev.meowmoments.di.UseCaseModule
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import com.ilyadev.meowmoments.testing.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.annotation.Config

@HiltAndroidTest
@UninstallModules(RepositoryModule::class, UseCaseModule::class)
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class, qualifiers = "w480dp-h800dp")
class MainFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: CatFactsRepository = mock(CatFactsRepository::class.java)

    @BindValue
    @JvmField
    val getTodayFactUseCase: GetTodayFactUseCase = mock(GetTodayFactUseCase::class.java)

    private val testFact = CatFact(
        id = 1L,
        text = "Test fact",
        category = "Test",
        imageUrl = null,
        dateReceived = "2026-05-18"
    )

    @Before
    fun init() {
        hiltRule.inject()

        runBlocking {
            `when`(getTodayFactUseCase.invoke()).thenReturn(testFact)
            `when`(repository.getCollectedCountAsFlow()).thenReturn(flowOf(1))
            `when`(repository.getRandomFact()).thenReturn(testFact)
        }
    }

    @Test
    fun fragment_displays_fact_text_and_image() {
        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.tv_fact_text)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_fact_image)).check(matches(isDisplayed()))
    }

    @Test
    fun click_on_next_fact_button_refreshes_fact() {
        launchFragmentInHiltContainer<MainFragment>()

        onView(withId(R.id.btn_next_fact)).perform(scrollTo(), click())

        onView(withId(R.id.tv_fact_text)).check(matches(isDisplayed()))
    }
}
