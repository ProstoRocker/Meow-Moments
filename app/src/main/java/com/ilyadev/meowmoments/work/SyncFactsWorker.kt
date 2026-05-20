package com.ilyadev.meowmoments.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ilyadev.meowmoments.data.repository.CatFactsRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncFactsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CatFactsRepositoryImpl // Через Hilt
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            // Вызываем метод синхронизации
            repository.syncFacts()
            Result.success()
        } catch (e: Exception) {
            // Если ошибка, можно попробовать позже
            Result.retry()
        }
    }
}