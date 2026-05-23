package com.ilyadev.meowmoments.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilyadev.meowmoments.data.local.database.AppDatabase
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

/** Integration Tests (Интеграционные тесты)
Цель:
- Проверить взаимодействие между слоями
- Проверить работу Repository с DAO
- Проверить работу ViewModel с Repository
 */

@RunWith(AndroidJUnit4::class)
class CatFactsRepositoryIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CatFactsRepositoryImpl
    private val apiService = mock(CatFactsApiService::class.java)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repository = CatFactsRepositoryImpl(
            database.catFactDao(),
            database.collectedFactDao(),
            database.recentlyViewedFactDao(),
            apiService
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `save fact to dao and retrieve via repository`() = runTest {
        // Given
        val factEntity =
            CatFactEntity(
                id = 1,
                text = "Integration test fact",
                imageUrl = null,
                category = "Test"
            )
        val collectedEntity = CollectedFactEntity(factId = 1, dateCollected = "2023-10-27")

        // When
        database.catFactDao().insertAll(listOf(factEntity))
        database.collectedFactDao().insert(collectedEntity)
        val retrievedFacts = repository.getAllCollectedFacts().first()

        // Then
        assertTrue(retrievedFacts.any { it.text == factEntity.text })
    }

    @Test
    fun `update favorite status in dao reflects in repository`() = runTest {
        // Given
        val factEntity = CatFactEntity(
            text = "Favorite test fact",
            imageUrl = null,
            category = "Test",
            isFavorite = false
        )
        database.catFactDao().insertAll(listOf(factEntity))
        val insertedId = database.catFactDao().getAllFacts().first().first().id

        // When
        repository.updateFavoriteStatus(insertedId, true)
        val updatedFacts = repository.getFavoriteFacts().first()

        // Then
        assertTrue(updatedFacts.any { it.id == insertedId && it.isFavorite })
    }
}
