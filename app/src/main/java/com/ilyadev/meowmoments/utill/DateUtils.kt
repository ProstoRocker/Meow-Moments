package com.ilyadev.meowmoments.utill

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    // Можно добавить другие полезные функции для работы с датами
}