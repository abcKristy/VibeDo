package com.example.vibedo.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.vibedo.view.theme.CardColorManager
import com.example.vibedo.view.theme.CardColorPair
import com.example.vibedo.view.theme.getAllAvailableColors
import com.example.vibedo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

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

    // Время
    var startTime by remember { mutableStateOf<Long?>(null) }
    var endTime by remember { mutableStateOf<Long?>(null) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Цвет
    var colorIndex by remember { mutableStateOf(0) }
    val allColors = getAllAvailableColors()

    // Для добавления нового тега
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColorIndex by remember { mutableStateOf(0) }

    // Загружаем пользовательские теги
    val customTags by viewModel.customTags.collectAsState(initial = emptyList())

    // Все теги: предопределенные + пользовательские
    val allTags = remember(customTags) {
        CardColorManager.predefinedTags + customTags.map { it.name }
    }

    // Автоматически выбираем дефолтный цвет для выбранного тега
    LaunchedEffect(tag) {
        if (CardColorManager.isPredefinedTag(tag)) {
            val defaultColor = CardColorManager.getDefaultForTag(tag)
            colorIndex = CardColorManager.getColorIndex(defaultColor)
        } else {
            // Для пользовательского тега находим его цвет
            val customTag = customTags.find { it.name == tag }
            customTag?.let {
                colorIndex = it.colorIndex
            }
        }
    }

    // Проверяем, нужно ли показывать палитру цветов
    val showColorPalette = tag == "task"

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
                                // Автоматически вычисляем продолжительность
                                val duration = if (startTime != null && endTime != null) {
                                    val diff = endTime!! - startTime!!
                                    (diff / (1000 * 60)).toInt() // минуты
                                } else {
                                    null
                                }

                                viewModel.addTask(
                                    title = title,
                                    description = description,
                                    priority = priority,
                                    tag = tag,
                                    startTime = startTime,
                                    endTime = endTime,
                                    duration = duration,
                                    colorIndex = colorIndex
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
                .verticalScroll(rememberScrollState())
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

            // Выбор времени
            Text(
                text = "Time",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Время начала
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { showStartTimePicker = true }
                ) {
                    Text(
                        text = startTime?.let {
                            SimpleDateFormat("h:mm a", Locale.ENGLISH).format(Date(it))
                        } ?: "Not set"
                    )
                }
            }

            // Время окончания
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "End",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { showEndTimePicker = true }
                ) {
                    Text(
                        text = endTime?.let {
                            SimpleDateFormat("h:mm a", Locale.ENGLISH).format(Date(it))
                        } ?: "Not set"
                    )
                }
            }

            // Показать продолжительность если указаны оба времени
            if (startTime != null && endTime != null) {
                val duration = (endTime!! - startTime!!) / (1000 * 60) // в минутах
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        val hours = duration / 60
                        val minutes = duration % 60
                        val durationText = when {
                            hours > 0 && minutes > 0 -> "${hours}h ${minutes}min"
                            hours > 0 -> "${hours}h"
                            else -> "${minutes}min"
                        }

                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Выбор категории (tag) с LazyRow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Кнопка добавления нового тега
                IconButton(
                    onClick = { showAddTagDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add new tag"
                    )
                }
            }

            // LazyRow с тегами
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Показываем все теги
                items(allTags) { currentTag ->
                    TagChip(
                        text = currentTag.replaceFirstChar { it.uppercase() },
                        selected = tag == currentTag,
                        onClick = { tag = currentTag }
                    )
                }
            }

            // Выбор цвета карточки (только для дефолтного тега "task")
            if (showColorPalette) {
                Text(
                    text = "Card Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Компактная палитра (7x3)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(allColors) { colorPair ->
                        CompactColorOption(
                            colorPair = colorPair,
                            isSelected = CardColorManager.getColorIndex(colorPair) == colorIndex,
                            onClick = {
                                colorIndex = CardColorManager.getColorIndex(colorPair)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

        // Упрощенные диалоги выбора времени
        if (showStartTimePicker) {
            TimeSelectionDialog(
                title = "Select Start Time",
                onConfirm = {
                    startTime = System.currentTimeMillis()
                    showStartTimePicker = false
                },
                onDismiss = { showStartTimePicker = false }
            )
        }

        if (showEndTimePicker) {
            TimeSelectionDialog(
                title = "Select End Time",
                onConfirm = {
                    endTime = (startTime ?: System.currentTimeMillis()) + 3600000 // +1 час
                    showEndTimePicker = false
                },
                onDismiss = { showEndTimePicker = false }
            )
        }

        // Диалог добавления нового тега
        if (showAddTagDialog) {
            AddTagDialog(
                tagName = newTagName,
                colorIndex = newTagColorIndex,
                allColors = allColors,
                onTagNameChange = { newTagName = it },
                onColorIndexChange = { newTagColorIndex = it },
                onConfirm = {
                    if (newTagName.isNotBlank()) {
                        viewModel.addCustomTag(newTagName, newTagColorIndex)
                        tag = newTagName
                        newTagName = ""
                        showAddTagDialog = false
                    }
                },
                onDismiss = {
                    showAddTagDialog = false
                    newTagName = ""
                }
            )
        }
    }
}

@Composable
fun AddTagDialog(
    tagName: String,
    colorIndex: Int,
    allColors: List<CardColorPair>,
    onTagNameChange: (String) -> Unit,
    onColorIndexChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Tag") },
        text = {
            Column {
                // Поле для названия тега
                OutlinedTextField(
                    value = tagName,
                    onValueChange = onTagNameChange,
                    label = { Text("Tag Name") },
                    placeholder = { Text("Enter tag name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = tagName.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Палитра цветов для тега
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(allColors) { colorPair ->
                        CompactColorOption(
                            colorPair = colorPair,
                            isSelected = CardColorManager.getColorIndex(colorPair) == colorIndex,
                            onClick = {
                                onColorIndexChange(CardColorManager.getColorIndex(colorPair))
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = tagName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CompactColorOption(
    colorPair: com.example.vibedo.view.theme.CardColorPair,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(colorPair.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = colorPair.contentColor,
                modifier = Modifier.size(16.dp)
            )
        }

        // Кликабельная область
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (!isSelected) {
                // Прозрачный слой для кликабельности
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Composable
fun TimeSelectionDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text("For demo using current time. Add a real time picker later.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Set Time")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
    // Для предопределенных тегов используем фиксированные цвета
    val backgroundColor = when (text.lowercase()) {
        "task" -> Color(0xFFE8F5E8)
        "meeting" -> Color(0xFFE3F2FD)
        "lesson" -> Color(0xFFF3E5F5)
        "work" -> Color(0xFFFFF3E0)
        "personal" -> Color(0xFFFCE4EC)
        "urgent" -> Color(0xFFFFEBEE)
        else -> {
            // Для пользовательских тегов - безопасный расчет индекса
            val index = (text.hashCode() and Int.MAX_VALUE) % 20 // Используем and Int.MAX_VALUE для получения положительного числа
            CardColorManager.getColorByIndex(index).backgroundColor
        }
    }

    val contentColor = when (text.lowercase()) {
        "task" -> Color(0xFF2E7D32)
        "meeting" -> Color(0xFF1565C0)
        "lesson" -> Color(0xFF7B1FA2)
        "work" -> Color(0xFFF57C00)
        "personal" -> Color(0xFFC2185B)
        "urgent" -> Color(0xFFD32F2F)
        else -> {
            // Для пользовательских тегов - безопасный расчет индекса
            val index = (text.hashCode() and Int.MAX_VALUE) % 20
            CardColorManager.getColorByIndex(index).contentColor
        }
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