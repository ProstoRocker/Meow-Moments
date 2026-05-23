package com.ilyadev.meowmoments.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.dao.CollectedFactDao
import com.ilyadev.meowmoments.data.local.dao.RecentlyViewedFactDao
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import com.ilyadev.meowmoments.util.CataasUtils
import com.ilyadev.meowmoments.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatFactsRepositoryImpl @Inject constructor(
    private val catFactDao: CatFactDao,
    private val collectedFactDao: CollectedFactDao,
    private val recentlyViewedFactDao: RecentlyViewedFactDao,
    private val catFactsApiService: CatFactsApiService
) : CatFactsRepository {

    override suspend fun getFactForToday(): CatFact? {
        val today = DateUtils.getCurrentDate()

        // Проверяем, есть ли уже собранный факт на сегодня
        val collectedFactsToday = collectedFactDao.getFactsForDate(today).first()
        if (collectedFactsToday.isNotEmpty()) {
            // Если факт уже собран сегодня, возвращаем его
            val factEntity =
                catFactDao.getFactById(collectedFactsToday.first().factId) ?: return null
            return mapToDomain(factEntity, today)
        }

        // Получаем все факты
        val allFacts = catFactDao.getAllFacts().first()
        if (allFacts.isEmpty()) {
            // Если в базе нет фактов, пробуем синхронизировать
            syncFacts()
            // Повторно пытаемся получить факты
            val updatedFacts = catFactDao.getAllFacts().first()
            if (updatedFacts.isEmpty()) return null
            return updatedFacts.random().let { mapToDomain(it, today) }
        }

        // Фильтруем факты, которые еще не собраны
        val uncollectedFacts = allFacts.filter { fact ->
            collectedFactDao.getCollectedFactByFactId(fact.id) == null
        }

        // Если есть несобранные факты, выбираем случайный
        val randomFact = if (uncollectedFacts.isNotEmpty()) {
            uncollectedFacts.random()
        } else {
            // Если все факты собраны, выбираем любой случайный
            allFacts.random()
        }

        // Сохраняем информацию о том, что этот факт собран сегодня
        collectedFactDao.insert(CollectedFactEntity(factId = randomFact.id, dateCollected = today))

        return mapToDomain(randomFact, today)
    }

    override fun getAllCollectedFacts(): Flow<List<CatFact>> {
        return collectedFactDao.getAllCollectedFacts().map { collectedEntities ->
            collectedEntities.map { collected ->
                val factEntity = catFactDao.getFactById(collected.factId) ?: return@map null
                mapToDomain(factEntity, collected.dateCollected)
            }.filterNotNull()
        }
    }

    // --- НОВЫЙ МЕТОД ДЛЯ ПАГИНАЦИИ ---
    override fun getPagedCollectedFacts(): Flow<PagingData<CatFact>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { catFactDao.getPagedCatFacts() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                // Преобразуем сущность в доменную модель
                CatFact(
                    id = entity.id,
                    text = entity.text,
                    category = entity.category,
                    imageUrl = entity.imageUrl,
                    dateReceived = DateUtils.getCurrentDate(), // или дата из сущности
                    isFavorite = entity.isFavorite
                )
            }
        }
    }

    override suspend fun getCollectedCount(): Int {
        return collectedFactDao.getCollectedCount()
    }

    override fun getCollectedCountAsFlow(): Flow<Int> {
        return collectedFactDao.getCollectedCountAsFlow()
    }

    // --- УЛУЧШЕННЫЙ МЕТОД ДЛЯ OFFLINE-FIRST ---
    override suspend fun getRandomFact(): CatFact? {
        // Сначала пробуем получить случайный факт из локальной базы
        val localFacts = catFactDao.getAllFacts().first()
        if (localFacts.isNotEmpty()) {
            val randomLocalFact = localFacts.random()
            return mapToDomain(randomLocalFact, DateUtils.getCurrentDate())
        }

        // Если в базе нет фактов, пробуем синхронизировать
        try {
            syncFacts()
            // После синхронизации снова пробуем получить из базы
            val updatedFacts = catFactDao.getAllFacts().first()
            if (updatedFacts.isNotEmpty()) {
                return mapToDomain(updatedFacts.random(), DateUtils.getCurrentDate())
            }
        } catch (e: Exception) {
            Log.e("CatFactsRepository", "Failed to sync facts for getRandomFact", e)
            // Если синхронизация не удалась, возвращаем null
        }

        return null
    }

    // --- Методы для избранного ---
    override fun getFavoriteFacts(): Flow<List<CatFact>> {
        return catFactDao.getFavoriteFacts().map { entities ->
            entities.map { entity ->
                mapToDomain(entity, DateUtils.getCurrentDate())
            }
        }
    }

    override suspend fun updateFavoriteStatus(factId: Long, isFavorite: Boolean) {
        catFactDao.updateFavoriteStatus(factId, isFavorite)
    }

    // --- Методы для последних просмотренных ---
    override suspend fun markFactAsViewed(factId: Long) {
        // Вставляем или заменяем запись в истории просмотров
        recentlyViewedFactDao.insertViewedFact(
            com.ilyadev.meowmoments.data.local.entities.RecentlyViewedFactEntity(factId = factId)
        )
    }

    override fun getRecentlyViewedFacts(limit: Int): Flow<List<CatFact>> {
        return recentlyViewedFactDao.getRecentlyViewedFactIds(limit)
            .map { ids ->
                if (ids.isEmpty()) {
                    emptyList()
                } else {
                    // Получаем все соответствующие факты
                    val factsMap = catFactDao.getFactsByIds(ids).first().associateBy { it.id }
                    // Возвращаем их в порядке, определенном списком ids
                    ids.mapNotNull { id -> factsMap[id] }.map { entity ->
                        // Не забываем преобразовать в доменную модель
                        CatFact(
                            id = entity.id,
                            text = entity.text,
                            category = entity.category,
                            imageUrl = entity.imageUrl,
                            dateReceived = DateUtils.getCurrentDate(), // Или дата добавления в историю, если нужно
                            isFavorite = entity.isFavorite
                        )
                    }
                }
            }
    }

    // --- НОВЫЙ МЕТОД ДЛЯ ПОИСКА ---
    override fun searchFacts(query: String): Flow<List<CatFact>> {
        return catFactDao.searchFactsByText(query).map { entities ->
            entities.map { entity ->
                CatFact(
                    id = entity.id,
                    text = entity.text,
                    category = entity.category,
                    imageUrl = entity.imageUrl,
                    dateReceived = DateUtils.getCurrentDate(), // или дата добавления в коллекцию
                    isFavorite = entity.isFavorite
                )
            }
        }
    }

    // --- НОВЫЙ МЕТОД ДЛЯ OFFLINE-FIRST СИНХРОНИЗАЦИИ ---
    suspend fun syncFacts() {
        try {
            val response = catFactsApiService.getFacts(10) // Загружаем 10 новых фактов
            if (response.isSuccessful && response.body() != null) {
                val apiFacts = response.body()!!.facts
                val entitiesToInsert = apiFacts.map { dto ->
                    com.ilyadev.meowmoments.data.local.entities.CatFactEntity(
                        text = dto.fact,
                        imageUrl = CataasUtils.generateCataasUrl(dto.fact),
                        category = "API" // или как-то иначе определять категорию
                    )
                }
                // Сохраняем в базу данных
                catFactDao.insertAll(entitiesToInsert)
            } else {
                Log.e("CatFactsRepository", "API request failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("CatFactsRepository", "Failed to sync facts", e)
            // Не бросаем исключение, чтобы не прерывать работу приложения
        }
    }

    /**
     * Загружает факты из Cat Facts API и преобразует их в локальные сущности
     */
    suspend fun loadFactsFromApi(limit: Int): List<com.ilyadev.meowmoments.data.local.entities.CatFactEntity> {
        return try {
            val response = catFactsApiService.getFacts(limit)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.facts.map { dto ->
                    com.ilyadev.meowmoments.data.local.entities.CatFactEntity(
                        text = dto.fact,
                        imageUrl = CataasUtils.generateCataasUrl(dto.fact)
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun mapToDomain(
        entity: com.ilyadev.meowmoments.data.local.entities.CatFactEntity,
        dateCollected: String
    ): CatFact {
        return CatFact(
            id = entity.id,
            text = entity.text,
            category = entity.category,
            imageUrl = entity.imageUrl,
            dateReceived = dateCollected,
            isFavorite = entity.isFavorite
        )
    }
}