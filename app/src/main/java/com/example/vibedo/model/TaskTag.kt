package com.example.vibedo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_tags")
data class TaskTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_index")
    val colorIndex: Int = 0,

    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = true,

    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis()
)