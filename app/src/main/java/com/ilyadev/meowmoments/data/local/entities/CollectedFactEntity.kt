package com.ilyadev.meowmoments.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collected_facts",
    foreignKeys = [
        ForeignKey(
            entity = CatFactEntity::class,
            parentColumns = ["id"],
            childColumns = ["factId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["factId"], unique = true)]
)
data class CollectedFactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val factId: Long,
    val dateCollected: String // формат "yyyy-MM-dd"
)