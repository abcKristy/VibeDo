package com.example.vibedo.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
    @Query("SELECT * FROM tasks ORDER BY created_date DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id= :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deletetask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY priority DESC, created_date DESC")
    fun getActiveTasks():Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET is_completed = :isCompleted WHERE id =:taskId")
    suspend fun updateCompletedStatus(taskId: Long, isCompleted:Boolean)

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY created_date DESC")
    fun getCompletedTasks():Flow<List<TaskEntity>>
}