package com.example.vibedo.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
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
import kotlin.math.roundToInt


@Composable
fun MainScreen(
    viewModel: TaskViewModel,
    onNavigateToAddTask: () -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    var isTodayView by remember { mutableStateOf(true) }

    // Состояние для смещения области с задачами
    var offsetY by remember { mutableStateOf(0f) }
    val maxOffset = with(LocalDensity.current) { 280.dp.toPx() } // Уменьшил с 400 до 280

    // Состояние для LazyColumn
    val listState = rememberLazyListState()

    // NestedScrollConnection для обработки скролла
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // Если скроллим вверх (delta > 0) и еще не достигли максимума
                if (delta > 0 && offsetY < maxOffset) {
                    val newOffset = (offsetY + delta).coerceAtMost(maxOffset)
                    offsetY = newOffset
                    return Offset(0f, delta) // Потребляем скролл
                }
                // Если скроллим вниз (delta < 0) и есть смещение
                else if (delta < 0 && offsetY > 0) {
                    val newOffset = (offsetY + delta).coerceAtLeast(0f)
                    offsetY = newOffset
                    return Offset(0f, delta) // Потребляем скролл
                }

                return Offset.Zero // Не потребляем скролл
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                // Автоматическое возвращение при быстром скролле
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Фон с белым цветом для области задач (с закругленными верхними углами)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)) // Закругленные верхние углы
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Контент (список задач или календарь)
            if (isTodayView) {
                if (tasks.isEmpty()) {
                    EmptyTaskState()
                } else {
                    TaskList(tasks = tasks, viewModel = viewModel, listState = listState)
                }
            } else {
                CalendarView(tasks, viewModel)
            }
        }

        // Верхняя панель (кнопки + хедер)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, (offsetY - maxOffset).roundToInt()) }
                .background(whiteMilk)
        ) {
            // Панель с кнопками переключения и добавления
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

            // Хедер ПОД кнопками
            if (isTodayView) {
                TodayHeader()
            } else {
                // Для CalendarView - адаптированный хедер или отступ
                Spacer(modifier = Modifier.height(30.dp))
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

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
            .padding(top = 50.dp)
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
    viewModel: TaskViewModel?,
    listState: LazyListState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
        contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp) // Добавил отступ снизу
    ) {
        items(tasks) { task ->
            // Проверяем, есть ли время в задаче
            val hasStartTime = task.startTime != null
            val hasEndTime = task.endTime != null

            TaskItem(
                task = task,
                showTimeLabels = hasStartTime || hasEndTime,
                onDeleteClick = {
                    viewModel?.deleteTask(task)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayViewEmptyPreview() {
    VibeDoTheme {
        Surface {
            val mockViewModel = remember {
                object : TaskViewModel(
                    repository = object : ITaskRepository {
                        override fun getAllTasks(): Flow<List<TaskEntity>> = MutableStateFlow(emptyList())
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