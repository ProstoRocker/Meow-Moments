package com.ilyadev.meowmoments.data.repository

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
        if (allFacts.isEmpty()) return null

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

    override suspend fun getCollectedCount(): Int {
        return collectedFactDao.getCollectedCount()
    }

    override fun getCollectedCountAsFlow(): Flow<Int> {
        return collectedFactDao.getCollectedCountAsFlow()
    }

    override suspend fun getRandomFact(): CatFact? {
        val allFacts = catFactDao.getAllFacts().first()
        if (allFacts.isEmpty()) return null
        val randomEntity = allFacts.random()
        return mapToDomain(
            randomEntity,
            DateUtils.getCurrentDate()
        )
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