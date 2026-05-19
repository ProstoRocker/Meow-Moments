package com.ilyadev.meowmoments.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_facts")
data class RecentlyViewedFactEntity(
    @PrimaryKey val factId: Long,
    val viewedAt: Long = System.currentTimeMillis() // Время просмотра в миллисекундах
)