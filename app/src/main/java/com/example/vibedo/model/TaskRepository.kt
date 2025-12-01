package com.example.vibedo.model

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    fun getActiveTasks(): Flow<List<TaskEntity>> = taskDao.getActiveTasks()
    fun getCompletedTasks(): Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    suspend fun getTaskById(taskId: Long): TaskEntity? = taskDao.getTaskById(taskId)
    suspend fun insertTask(task: TaskEntity):Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun updateCompletedStatus(taskId: Long, isCompleted: Boolean){
        taskDao.updateCompletedStatus(taskId,isCompleted)
    }
}