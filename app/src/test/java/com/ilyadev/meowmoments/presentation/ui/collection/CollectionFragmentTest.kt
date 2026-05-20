package com.ilyadev.meowmoments.presentation.ui.collection

import androidx.paging.PagingData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.di.RepositoryModule
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.testing.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class)
class CollectionFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: CatFactsRepository = mock(CatFactsRepository::class.java)

    @Before
    fun init() {
        hiltRule.inject()
        // Mock default behavior for paged facts used in init
        whenever(repository.getPagedCollectedFacts()).thenReturn(flowOf(PagingData.empty()))
        whenever(repository.getAllCollectedFacts()).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun recycler_view_is_displayed() {
        launchFragmentInHiltContainer<CollectionFragment>()

        onView(withId(R.id.rv_facts)).check(matches(hasMinimumChildCount(0)))
    }
}
