package com.example.vibedo.model

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
): ITaskRepository {
    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    override fun getActiveTasks(): Flow<List<TaskEntity>> = taskDao.getActiveTasks()
    override fun getCompletedTasks(): Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    override suspend fun getTaskById(taskId: Long): TaskEntity? = taskDao.getTaskById(taskId)
    override suspend fun insertTask(task: TaskEntity):Long = taskDao.insertTask(task)
    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    override suspend fun updateCompletedStatus(taskId: Long, isCompleted: Boolean){
        taskDao.updateCompletedStatus(taskId,isCompleted)
    }
}

interface ITaskRepository{
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getActiveTasks(): Flow<List<TaskEntity>>
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: Long): TaskEntity?
    suspend fun insertTask(task: TaskEntity):Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun updateCompletedStatus(taskId: Long, isCompleted: Boolean)
}