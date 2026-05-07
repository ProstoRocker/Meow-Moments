package com.ilyadev.meowmoments.data.remote.api

import com.ilyadev.meowmoments.data.remote.dto.CatFactApiDto
import com.ilyadev.meowmoments.data.remote.dto.CatFactsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CatFactsApiService {

    /**
     * Получает один случайный факт о котах.
     */
    @GET("fact")
    suspend fun getRandomFact(): Response<CatFactApiDto>

    /**
     * Получает несколько фактов о котах.
     * @param limit Количество фактов (макс. 500)
     */
    @GET("facts")
    suspend fun getFacts(
        @Query("limit") limit: Int
    ): Response<CatFactsResponse>
}