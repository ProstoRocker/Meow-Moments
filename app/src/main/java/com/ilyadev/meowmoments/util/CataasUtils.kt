package com.ilyadev.meowmoments.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CataasUtils {

    /**
     * Генерирует URL для Cataas с текстом факта.
     * @param fact Текст факта, который будет отображаться на изображении
     * @return URL изображения кота с текстом
     */
    fun generateCataasUrl(fact: String): String {
        // 1. Оставляем только буквы, цифры и пробелы (убираем всё остальное)
        val cleanText = fact
            .takeIf { it.length <= 35 } ?: fact.substring(0, 35)
            .replace(Regex("[^a-zA-Z0-9\\s]"), "") // Убираем все спецсимволы
            .trim()
            .let { if (it.isEmpty()) "Cat Fact" else it }

        // 2. Кодируем только безопасную строку
        val encoded = URLEncoder.encode(cleanText, StandardCharsets.UTF_8.name())

        // 3. Формируем URL
        return "https://cataas.com/cat/says/$encoded?size=medium&color=white"
    }
}