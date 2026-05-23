package com.ilyadev.meowmoments.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

// Тесты для Utility Functions

class DateUtilsTest {

    @Test
    fun `getCurrentDate returns today's date in yyyy-MM-dd format`() {
        val expectedFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())

        val result = DateUtils.getCurrentDate()

        assertEquals(expectedFormat, result)
    }

    @Test
    fun `isValidDateFormat returns true for valid date string`() {
        val validDateString = "2026-05-18"
        assertTrue(DateUtils.isValidDateFormat(validDateString))
    }

    @Test
    fun `isValidDateFormat returns false for invalid format`() {
        assertFalse(DateUtils.isValidDateFormat("18/05/2026"))
        assertFalse(DateUtils.isValidDateFormat("2026.05.18"))
        assertFalse(DateUtils.isValidDateFormat("2026-05-18 10:00"))
    }

    @Test
    fun `isValidDateFormat returns false for invalid values`() {
        assertFalse(DateUtils.isValidDateFormat("2026-13-18")) // Invalid month
        assertFalse(DateUtils.isValidDateFormat("2026-05-32")) // Invalid day
        assertFalse(DateUtils.isValidDateFormat("2023-02-29")) // Not a leap year
    }

    @Test
    fun `isValidDateFormat returns true for leap year`() {
        assertTrue(DateUtils.isValidDateFormat("2024-02-29")) // Leap year
    }

    @Test
    fun `isValidDateFormat returns false for empty or blank string`() {
        assertFalse(DateUtils.isValidDateFormat(""))
        assertFalse(DateUtils.isValidDateFormat("   "))
    }

    @Test
    fun `formatToDisplayDate returns formatted date for valid input`() {
        val result = DateUtils.formatToDisplayDate("2026-05-18")
        // Expected format depends on locale, but for default/US it would be "May 18, 2026"
        // Since we use Locale.getDefault() in the code, we should be careful.
        // For testing purposes, we can check if it contains the expected year and day.
        assertTrue(result.contains("18"))
        assertTrue(result.contains("2026"))
    }

    @Test
    fun `formatToDisplayDate returns empty string for invalid input`() {
        assertEquals("", DateUtils.formatToDisplayDate("invalid-date"))
        assertEquals("", DateUtils.formatToDisplayDate(""))
    }
}
