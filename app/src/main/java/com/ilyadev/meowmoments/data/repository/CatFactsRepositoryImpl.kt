package com.ilyadev.meowmoments.data.repository

import android.text.format.DateUtils
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.local.dao.CollectedFactDao
import com.ilyadev.meowmoments.data.local.entities.CollectedFactEntity
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatFactsRepositoryImpl @Inject constructor(
    private val catFactDao: CatFactDao,
    private val collectedFactDao: CollectedFactDao
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

        // Фильтруем факты, которые еще не собраны
        val uncollectedFacts = allFacts.filter { fact ->
            collectedFactDao.getFactsForDate(fact.id).first().isEmpty()
        }

        // Если есть несобранные факты, выбираем случайный
        if (uncollectedFacts.isNotEmpty()) {
            val randomFact = uncollectedFacts.random()
            // Сохраняем информацию о том, что этот факт собран сегодня
            collectedFactDao.insert(
                CollectedFactEntity(
                    factId = randomFact.id,
                    dateCollected = today
                )
            )
            return mapToDomain(randomFact, today)
        }

        // Если все факты собраны, возвращаем null или можно вернуть случайный из уже собранных
        return null
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

    private fun mapToDomain(
        entity: com.ilyadev.meowmoments.data.local.entities.CatFactEntity,
        dateCollected: String
    ): CatFact {
        return CatFact(
            id = entity.id,
            text = entity.text,
            category = entity.category,
            imageUrl = entity.imageUrl,
            dateReceived = dateCollected
        )
    }
}