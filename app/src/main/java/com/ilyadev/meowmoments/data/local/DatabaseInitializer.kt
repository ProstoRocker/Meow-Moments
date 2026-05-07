package com.ilyadev.meowmoments.data.local

import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.data.remote.api.CatFactsApiService
import com.ilyadev.meowmoments.util.CataasUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val catFactDao: CatFactDao,
    private val catFactsApiService: CatFactsApiService
) {

    companion object {
        const val INITIAL_FACTS_COUNT = 50 // Количество фактов для загрузки при инициализации
    }

    suspend fun initializeDatabase() {
        withContext(Dispatchers.IO) {
            // Проверяем, есть ли уже данные в базе
            if (catFactDao.getCount() == 0) {
                // Загружаем факты из API
                val catFacts = loadFactsFromApi(INITIAL_FACTS_COUNT)
                // Сохраняем в базу
                catFactDao.insertAll(catFacts)
            }
        }
    }

    private suspend fun loadFactsFromApi(limit: Int): List<com.ilyadev.meowmoments.data.local.entities.CatFactEntity> {
        return try {
            val response = catFactsApiService.getFacts(limit)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.map { dto ->
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
}