package com.ilyadev.meowmoments.data.local

import android.content.Context
import com.ilyadev.meowmoments.data.local.dao.CatFactDao
import com.ilyadev.meowmoments.util.CatFactJsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val context: Context,
    private val catFactDao: CatFactDao
) {

    suspend fun initializeDatabase() {
        withContext(Dispatchers.IO) {
            // Проверяем, есть ли уже данные в базе
            if (catFactDao.getAllFacts().first().isEmpty()) {
                // Загружаем тестовые данные
                val catFacts = CatFactJsonParser.parseCatFacts(context)
                // Сохраняем в базу
                catFactDao.insertAll(catFacts)
            }
        }
    }
}