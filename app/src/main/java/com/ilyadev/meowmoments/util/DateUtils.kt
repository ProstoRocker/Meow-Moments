package com.ilyadev.meowmoments.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun isValidDateFormat(dateString: String): Boolean {
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            df.isLenient = false
            val parsedDate = df.parse(dateString) ?: return false
            dateString == df.format(parsedDate)
        } catch (_: Exception) {
            false
        }
    }

    fun formatToDisplayDate(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            date?.let { displayFormat.format(it) } ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}