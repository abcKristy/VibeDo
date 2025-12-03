package com.example.vibedo.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibedo.model.ITaskRepository
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.model.TaskTag
import com.example.vibedo.model.TaskTagDao
import com.example.vibedo.view.components.CalendarHeader
import com.example.vibedo.view.components.TaskItem
import com.example.vibedo.view.components.TodayHeader
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.view.theme.whiteMilk
import com.example.vibedo.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun MainScreen(
    viewModel: TaskViewModel,
    onNavigateToAddTask: () -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    var isTodayView by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = whiteMilk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Spacer(modifier = Modifier.height(60.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            color = if (isTodayView) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.background,
                            border = BorderStroke(
                                width = if (isTodayView) 0.dp else 1.dp,
                                color = if (isTodayView) Color.Transparent else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .clickable { isTodayView = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Today",
                                    fontSize = 18.sp,
                                    color = if (isTodayView) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            color = if (!isTodayView) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.background,
                            border = BorderStroke(
                                width = if (!isTodayView) 0.dp else 1.dp,
                                color = if (!isTodayView) Color.Transparent else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .height(44.dp)
                                .clickable { isTodayView = false }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Calendar",
                                    fontSize = 18.sp,
                                    color = if (!isTodayView) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(0.5f))
                            .clickable(onClick = onNavigateToAddTask),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isTodayView) {
                TodayView(tasks, viewModel)
            } else {
                CalendarView(tasks, viewModel)
            }
        }
    }
}

@Composable
fun TodayView(
    tasks: List<TaskEntity>,
    viewModel: TaskViewModel?
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Заголовок Today
        TodayHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Список задач
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (tasks.isEmpty()) {
                EmptyTaskState()
            } else {
                TaskList(tasks, viewModel)
            }
        }
    }
}

@Composable
fun CalendarView(
    tasks: List<TaskEntity>,
    viewModel: TaskViewModel?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalendarHeader()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Calendar view will be here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyTaskState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No tasks for today",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap + button to add your first task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TaskList(
    tasks: List<TaskEntity>,
    viewModel: TaskViewModel?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            // Проверяем, есть ли время в задаче
            val hasStartTime = task.startTime != null
            val hasEndTime = task.endTime != null

            TaskItem(
                task = task,
                showTimeLabels = hasStartTime || hasEndTime, // Показывать метки времени только если есть хоть одно время
                onDeleteClick = {
                    viewModel?.deleteTask(task)
                }
            )
        }
    }
}

// Если нужно также обновить компонент TaskItem, вот как его нужно модифицировать:
// @Composable
// fun TaskItem(
//     task: TaskEntity,
//     showTimeLabels: Boolean = true, // Добавить этот параметр
//     onDeleteClick: () -> Unit
// ) {
//     // Внутри компонента TaskItem проверяйте showTimeLabels перед отображением меток "Start"/"End"
// }

@Preview(showBackground = true)
@Composable
fun TodayViewEmptyPreview() {
    VibeDoTheme {
        Surface {
            TodayView(
                tasks = emptyList(),
                viewModel = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarViewPreview() {
    VibeDoTheme {
        Surface {
            CalendarView(
                tasks = emptyList(),
                viewModel = null
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun MainScreenWithTasksPreview() {
    VibeDoTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val mockViewModel = remember {
                object : TaskViewModel(
                    repository = object : ITaskRepository {
                        override fun getAllTasks(): Flow<List<TaskEntity>> = MutableStateFlow(
                            listOf(
                                // Задача с временем начала и конца
                                TaskEntity(
                                    id = 1,
                                    title = "Team meeting",
                                    description = "Discuss project progress",
                                    priority = 2,
                                    tag = "meeting",
                                    colorIndex = 4,
                                    startTime = System.currentTimeMillis(),
                                    endTime = System.currentTimeMillis() + 3600000,
                                    duration = 60
                                ),
                                // Задача только с временем окончания
                                TaskEntity(
                                    id = 2,
                                    title = "Gym workout",
                                    description = "Cardio and weights",
                                    priority = 1,
                                    tag = "workout",
                                    colorIndex = 2,
                                    startTime = null,
                                    endTime = System.currentTimeMillis() + 10800000,
                                    duration = 60
                                ),
                                // Задача без времени
                                TaskEntity(
                                    id = 3,
                                    title = "Study session",
                                    description = "Machine learning course",
                                    priority = 1,
                                    tag = "lesson",
                                    colorIndex = 10,
                                    startTime = null,
                                    endTime = null,
                                    duration = null
                                )
                            )
                        )
                        override fun getActiveTasks(): Flow<List<TaskEntity>> = MutableStateFlow(emptyList())
                        override fun getCompletedTasks(): Flow<List<TaskEntity>> = MutableStateFlow(emptyList())
                        override suspend fun getTaskById(taskId: Long): TaskEntity? = null
                        override suspend fun insertTask(task: TaskEntity): Long = 0
                        override suspend fun updateTask(task: TaskEntity) {}
                        override suspend fun deleteTask(task: TaskEntity) {}
                        override suspend fun updateCompletedStatus(taskId: Long, isCompleted: Boolean) {}
                    },
                    taskTagDao = object : TaskTagDao {
                        override fun getAllTags(): Flow<List<TaskTag>> = MutableStateFlow(emptyList())
                        override fun getCustomTags(): Flow<List<TaskTag>> = MutableStateFlow(emptyList())
                        override suspend fun getTagByName(tagName: String): TaskTag? = null
                        override suspend fun insertTag(tag: TaskTag): Long = 0
                        override suspend fun updateTag(tag: TaskTag) {}
                        override suspend fun deleteTag(tag: TaskTag) {}
                        override suspend fun tagExists(tagName: String): Int = 0
                    }
                ) {}
            }

            MainScreen(
                viewModel = mockViewModel,
                onNavigateToAddTask = {}
            )
        }
    }
}