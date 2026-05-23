package com.ilyadev.meowmoments.util

import org.junit.Assert.assertTrue
import org.junit.Test

// Тесты для Utility Functions

class CataasUtilsTest {

    @Test
    fun `generateCataasUrl returns URL containing the text`() {
        val text = "Hello World"

        val result = CataasUtils.generateCataasUrl(text)

        assertTrue(result.contains("Hello"))
        assertTrue(result.contains("World"))
        assertTrue(result.startsWith("https://cataas.com/cat/says/"))
    }

    @Test
    fun `generateCataasUrl handles special characters in text`() {
        val text = "Hello, World! 123"

        val result = CataasUtils.generateCataasUrl(text)

        // Comma and Exclamation mark are removed by Regex("[^a-zA-Z0-9\\s]")
        assertTrue(result.contains("Hello"))
        assertTrue(result.contains("World"))
        assertTrue(result.contains("123"))
    }
}
