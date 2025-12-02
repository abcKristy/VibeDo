package com.example.vibedo.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTagDao {

    @Query("SELECT * FROM task_tags ORDER BY created_date DESC")
    fun getAllTags(): Flow<List<TaskTag>>

    @Query("SELECT * FROM task_tags WHERE is_custom = 1 ORDER BY created_date DESC")
    fun getCustomTags(): Flow<List<TaskTag>>

    @Query("SELECT * FROM task_tags WHERE name = :tagName")
    suspend fun getTagByName(tagName: String): TaskTag?

    @Insert
    suspend fun insertTag(tag: TaskTag): Long

    @Update
    suspend fun updateTag(tag: TaskTag)

    @Delete
    suspend fun deleteTag(tag: TaskTag)

    @Query("SELECT COUNT(*) FROM task_tags WHERE name = :tagName")
    suspend fun tagExists(tagName: String): Int
}