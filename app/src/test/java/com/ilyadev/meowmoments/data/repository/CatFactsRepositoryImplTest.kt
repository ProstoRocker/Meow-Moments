package com.ilyadev.meowmoments.data.repository

import android.util.Log
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.dao.CollectedFactDao
import com.ilyadev.meowmoments.data.local.dao.RecentlyViewedFactDao
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import com.ilyadev.meowmoments.data.remote.dto.CatFactApiDto
import com.ilyadev.meowmoments.data.remote.dto.CatFactsResponse
import com.ilyadev.meowmoments.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

// Тесты для Repository

class CatFactsRepositoryImplTest {

    private lateinit var repository: CatFactsRepositoryImpl

    @Mock
    private lateinit var catFactDao: CatFactDao

    @Mock
    private lateinit var collectedFactDao: CollectedFactDao

    @Mock
    private lateinit var recentlyViewedFactDao: RecentlyViewedFactDao

    @Mock
    private lateinit var apiService: CatFactsApiService

    private lateinit var closeable: AutoCloseable
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        mockedLog = mockStatic(Log::class.java)
        repository = CatFactsRepositoryImpl(
            catFactDao,
            collectedFactDao,
            recentlyViewedFactDao,
            apiService
        )
    }

    @After
    fun tearDown() {
        mockedLog.close()
        closeable.close()
    }

    @Test
    fun `getFactForToday returns fact from local db if available for today`() = runTest {
        // Given
        val today = DateUtils.getCurrentDate()
        val factId = 1L
        val mockCollectedFact = CollectedFactEntity(factId = factId, dateCollected = today)
        val mockFactEntity = CatFactEntity(id = factId, text = "Test Fact", category = "Test")

        whenever(collectedFactDao.getFactsForDate(today)).thenReturn(flowOf(listOf(mockCollectedFact)))
        whenever(catFactDao.getFactById(factId)).thenReturn(mockFactEntity)

        // When
        val result = repository.getFactForToday()

        // Then
        assertEquals("Test Fact", result?.text)
        verify(collectedFactDao).getFactsForDate(today)
        verify(catFactDao).getFactById(factId)
    }

    @Test
    fun `updateFavoriteStatus calls dao method with correct parameters`() = runTest {
        // Given
        val factId = 1L
        val isFavorite = true

        // When
        repository.updateFavoriteStatus(factId, isFavorite)

        // Then
        verify(catFactDao).updateFavoriteStatus(factId, isFavorite)
    }

    @Test
    fun `getRecentlyViewedFacts returns correct list based on dao`() = runTest {
        // Given
        val ids = listOf(1L, 2L)
        val entities = listOf(
            CatFactEntity(id = 1L, text = "Fact 1"),
            CatFactEntity(id = 2L, text = "Fact 2")
        )
        whenever(recentlyViewedFactDao.getRecentlyViewedFactIds(10)).thenReturn(flowOf(ids))
        whenever(catFactDao.getFactsByIds(ids)).thenReturn(flowOf(entities))

        // When
        val result = repository.getRecentlyViewedFacts(10).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Fact 1", result[0].text)
        assertEquals("Fact 2", result[1].text)
    }

    @Test
    fun `searchFacts returns facts matching query`() = runTest {
        // Given
        val query = "cat"
        val entities = listOf(
            CatFactEntity(id = 1L, text = "cat fact")
        )
        whenever(catFactDao.searchFactsByText(query)).thenReturn(flowOf(entities))

        // When
        val result = repository.searchFacts(query).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("cat fact", result[0].text)
    }

    @Test
    fun `syncFacts calls api service and inserts into dao`() = runTest {
        // Given
        val mockDto = CatFactApiDto(fact = "API Fact", length = 8)
        val mockResponse = CatFactsResponse(currentPage = 1, facts = listOf(mockDto), total = 1)
        whenever(apiService.getFacts(10)).thenReturn(Response.success(mockResponse))

        // When
        repository.syncFacts()

        // Then
        verify(apiService).getFacts(10)
        verify(catFactDao).insertAll(any())
    }
}