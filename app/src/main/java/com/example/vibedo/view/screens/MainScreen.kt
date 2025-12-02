package com.example.vibedo.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.navigation.NavHostController
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.view.components.CalendarHeader
import com.example.vibedo.view.components.TaskItem
import com.example.vibedo.view.components.TodayHeader
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TaskViewModel,
    onNavigateToAddTask: () -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    var isTodayView by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    // Кнопка Today
                    FilterChip(
                        selected = isTodayView,
                        onClick = { isTodayView = true },
                        label = { Text("Today") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Today,
                                contentDescription = "Today"
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Кнопка Calendar
                    FilterChip(
                        selected = !isTodayView,
                        onClick = { isTodayView = false },
                        label = { Text("Calendar") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Calendar"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Кнопка добавления
                    FloatingActionButton(
                        onClick = onNavigateToAddTask,
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onCheckedChange = { isCompleted ->
                    viewModel?.updateTaskCompleted(task.id, isCompleted)
                },
                onDeleteClick = {
                    viewModel?.deleteTask(task)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyTaskStatePreview() {
    VibeDoTheme {
        Surface {
            EmptyTaskState()
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun TodayViewPreview() {
    VibeDoTheme {
        Surface {
            TodayView(
                tasks = listOf(
                    TaskEntity(
                        id = 1,
                        title = "Meeting with team",
                        description = "Discuss project progress",
                        priority = 2
                    ),
                    TaskEntity(
                        id = 2,
                        title = "Lunch with friends",
                        isCompleted = true,
                        priority = 0
                    ),
                    TaskEntity(
                        id = 3,
                        title = "Workout session",
                        description = "Gym at 6 PM",
                        priority = 1
                    )
                ),
                viewModel = null
            )
        }
    }
}

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