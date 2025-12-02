package com.example.vibedo.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.vibedo.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(0) }
    var tag by remember { mutableStateOf("task") }
    var duration by remember { mutableStateOf("") } // Для ввода продолжительности

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                // Конвертируем duration из строки в число
                                val durationInt = duration.toIntOrNull()

                                viewModel.addTask(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    tag = tag,
                                    duration = durationInt
                                )
                                onNavigateBack()
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Поле для заголовка
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title*") },
                placeholder = { Text("Enter task title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                isError = title.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для описания
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("Enter task description") },
                singleLine = false,
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Выбор категории (tag)
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                TagChip(
                    text = "Task",
                    selected = tag == "task",
                    onClick = { tag = "task" }
                )

                TagChip(
                    text = "Meeting",
                    selected = tag == "meeting",
                    onClick = { tag = "meeting" }
                )

                TagChip(
                    text = "Lesson",
                    selected = tag == "lesson",
                    onClick = { tag = "lesson" }
                )
            }

            // Поле для продолжительности
            OutlinedTextField(
                value = duration,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                        duration = it
                    }
                },
                label = { Text("Duration (minutes, optional)") },
                placeholder = { Text("e.g., 30") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    if (duration.isNotEmpty()) {
                        Text("min", modifier = Modifier.padding(end = 8.dp))
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Выбор приоритета
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip(
                    text = "Low",
                    selected = priority == 0,
                    onClick = { priority = 0 }
                )

                PriorityChip(
                    text = "Medium",
                    selected = priority == 1,
                    onClick = { priority = 1 }
                )

                PriorityChip(
                    text = "High",
                    selected = priority == 2,
                    onClick = { priority = 2 }
                )
            }
        }
    }
}

@Composable
fun PriorityChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}

@Composable
fun TagChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when (text.lowercase()) {
        "task" -> Color(0xFFE8F5E8)
        "meeting" -> Color(0xFFE3F2FD)
        "lesson" -> Color(0xFFF3E5F5)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (text.lowercase()) {
        "task" -> Color(0xFF2E7D32)
        "meeting" -> Color(0xFF1565C0)
        "lesson" -> Color(0xFF7B1FA2)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) backgroundColor else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) contentColor else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        onClick = onClick,
        modifier = Modifier.height(36.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}