package com.example.vibedo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibedo.model.ITaskRepository
import com.example.vibedo.model.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: ITaskRepository
): ViewModel() {
    val allTasks: Flow<List<TaskEntity>> = repository.getAllTasks()
    val activeTasks: Flow<List<TaskEntity>> = repository.getActiveTasks()
    val completedTasks: Flow<List<TaskEntity>> = repository.getCompletedTasks()

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Empty)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private val _selectedTask = MutableStateFlow<TaskEntity?>(null)
    val selectedTask: StateFlow<TaskEntity?> = _selectedTask.asStateFlow()

    fun addTask(
        title: String,
        description: String? = null,
        priority: Int = 0,
        tag: String = "task",
        startTime: Long? = null,
        endTime: Long? = null,
        duration: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val task = TaskEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    tag = tag,
                    startTime = startTime,
                    endTime = endTime,
                    duration = duration
                )
                repository.insertTask(task)
                _uiState.value = TaskUiState.Success("Task added")
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error(e.message ?: "Cannot add task")
            }
        }
    }

    fun updateTaskCompleted(taskId:Long,isCompleted: Boolean){
        viewModelScope.launch {
            repository.updateCompletedStatus(taskId,isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity){
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addTask(
        title: String,
        description: String? = null,
        priority: Int = 0,
        tag: String = "task",
        duration: Int? = null
    ) {
        addTask(
            title = title,
            description = description,
            priority = priority,
            tag = tag,
            startTime = null,
            endTime = null,
            duration = duration
        )
    }
}

sealed class TaskUiState{
    object Empty: TaskUiState()
    object Loading: TaskUiState()
    data class Success(val message: String): TaskUiState()
    data class Error(val message: String): TaskUiState()
}