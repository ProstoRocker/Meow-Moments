package com.ilyadev.meowmoments.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CatFactsResponse(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("data")
    val facts: List<CatFactApiDto>,

    @SerializedName("total")
    val total: Int
)