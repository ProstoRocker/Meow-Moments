package com.ilyadev.meowmoments.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CataasUtils {

    /**
     * Генерирует URL для Cataas с текстом факта.
     * @param fact Текст факта, который будет отображаться на изображении
     * @param size Размер изображения (small, medium, large)
     * @param color Цвет текста (например, "white", "black", "orange")
     * @return URL изображения кота с текстом
     */
    fun generateCataasUrl(
        fact: String,
        size: String = "medium",
        color: String = "white"
    ): String {
        val encodedFact = URLEncoder.encode(fact, StandardCharsets.UTF_8.name())
        return "https://cataas.com/cat/says/$encodedFact?size=$size&color=$color"
    }

    /**
     * Генерирует URL для случайного изображения кота без текста
     */
    fun getRandomCatImageUrl(): String {
        return "https://cataas.com/cat"
    }
}