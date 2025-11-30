package com.example.vibedo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String?=null,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "priority")
    val priority: Int = 0,
    @ColumnInfo(name = "created_date")
    val createdDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null
)