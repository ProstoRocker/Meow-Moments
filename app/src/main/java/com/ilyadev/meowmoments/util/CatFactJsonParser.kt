package com.ilyadev.meowmoments.util

import android.content.Context
import com.ilyadev.meowmoments.data.local.entities.CatFactEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ilyadev.meowmoments.R
import java.io.InputStream
import java.io.InputStreamReader

object CatFactJsonParser {

    fun parseCatFacts(context: Context): List<CatFactEntity> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.cat_facts)
        val reader = InputStreamReader(inputStream)
        val catFactsType = object : TypeToken<List<CatFactJson>>() {}.type
        val catFactsJson = Gson().fromJson<List<CatFactJson>>(reader, catFactsType)

        return catFactsJson.map { json ->
            CatFactEntity(
                text = json.text,
                category = json.category,
                imageUrl = json.imageUrl
            )
        }
    }

    private data class CatFactJson(
        val text: String,
        val category: String,
        val imageUrl: String?
    )
}