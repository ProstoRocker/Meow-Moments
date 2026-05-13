package com.ilyadev.meowmoments.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель данных для факта о коте.
 * @property id Уникальный идентификатор факта (локальный или удалённый).
 * @property text Текст самого факта.
 * @property category Категория факта (например, "Интересный", "Смешной", "Порода").
 * @property imageUrl URL изображения, связанного с фактом (может быть null).
 * @property dateReceived Дата, когда факт был получен пользователем (в формате "yyyy-MM-dd").
 */
@Parcelize
data class CatFact(
    val id: Long,
    val text: String,
    val category: String,
    val imageUrl: String?,
    val dateReceived: String,
    val isFavorite: Boolean = false
) : Parcelable