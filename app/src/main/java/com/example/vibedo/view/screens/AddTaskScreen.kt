package com.example.vibedo.view.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.example.vibedo.model.ITaskRepository
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.model.TaskTag
import com.example.vibedo.model.TaskTagDao
import com.example.vibedo.view.theme.CardColorManager
import com.example.vibedo.view.theme.CardColorPair
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.view.theme.coralDark
import com.example.vibedo.view.theme.getAllAvailableColors
import com.example.vibedo.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(0) }
    var tag by remember { mutableStateOf("task") }

    // Время в формате строк (HH:MM)
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    // Ошибки валидации времени
    var startTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }

    // Флаги ошибок для пустых обязательных полей
    var titleError by remember { mutableStateOf(false) }
    var endTimeErrorFlag by remember { mutableStateOf(false) }

    // Для расчета продолжительности
    var duration by remember { mutableStateOf<Int?>(null) }

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

    // Валидация времени и расчет продолжительности
    LaunchedEffect(startTimeText, endTimeText) {
        // Валидация времени начала
        startTimeError = validateTime(startTimeText)

        // Валидация времени окончания
        endTimeError = validateTime(endTimeText)

        // Только если оба времени валидны, вычисляем продолжительность
        if (startTimeError == null && endTimeError == null &&
            startTimeText.isNotEmpty() && endTimeText.isNotEmpty()) {
            duration = calculateDuration(startTimeText, endTimeText)
        } else {
            duration = null
        }
    }

    // Проверяем, нужно ли показывать палитру цветов
    val showColorPalette = tag == "task"

    // Функция проверки валидации и сохранения
    fun validateAndSave() {
        // Сбрасываем флаги ошибок
        titleError = false
        endTimeErrorFlag = false

        // Проверяем обязательные поля
        var hasError = false

        if (title.isBlank()) {
            titleError = true
            hasError = true
        }

        if (endTimeText.isBlank()) {
            endTimeErrorFlag = true
            hasError = true
        }

        // Если есть ошибки валидации времени
        if (startTimeError != null || endTimeError != null) {
            hasError = true
        }

        if (hasError) {
            // Показываем тост с сообщением
            Toast.makeText(context, "Заполните название и время окончания", Toast.LENGTH_LONG).show()
            return
        }

        // Все проверки пройдены, сохраняем задачу
        val startTimeMillis = if (startTimeText.isNotEmpty()) {
            convertTimeToMillis(startTimeText)
        } else null

        val endTimeMillis = if (endTimeText.isNotEmpty()) {
            convertTimeToMillis(endTimeText)
        } else null

        viewModel.addTask(
            title = title,
            description = description,
            priority = priority,
            tag = tag,
            startTime = startTimeMillis,
            endTime = endTimeMillis,
            duration = duration,
            colorIndex = colorIndex
        )
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { validateAndSave() }
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
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Поле для заголовка
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false // Сбрасываем ошибку при вводе
                },
                label = { Text("Task Title*") },
                placeholder = { Text("Enter task title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text(
                            text = "Required",
                            color = coralDark
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (titleError) coralDark else MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = if (titleError) coralDark else MaterialTheme.colorScheme.outline,
                    focusedLabelColor = if (titleError) coralDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = if (titleError) coralDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    errorBorderColor = coralDark,
                    errorLabelColor = coralDark,
                    errorSupportingTextColor = coralDark
                )
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
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Start (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    SmartTimeInputField(
                        value = startTimeText,
                        onValueChange = { startTimeText = it },
                        placeholder = "09:00",
                        modifier = Modifier.width(120.dp),
                        hasValidationError = startTimeError != null
                    )
                }

                // Отображение ошибки для времени начала
                startTimeError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }

            // Время окончания
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "End*",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    SmartTimeInputField(
                        value = endTimeText,
                        onValueChange = {
                            endTimeText = it
                            endTimeErrorFlag = false // Сбрасываем ошибку при вводе
                        },
                        placeholder = "10:00",
                        modifier = Modifier.width(120.dp),
                        hasEmptyError = endTimeErrorFlag,
                        hasValidationError = endTimeError != null
                    )
                }

                // Отображение ошибки валидации для времени окончания
                endTimeError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                // Сообщение об обязательности поля
                if (endTimeErrorFlag) {
                    Text(
                        text = "Required",
                        color = coralDark,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }

            // Показать продолжительность если указаны оба времени и они валидны
            if (duration != null && duration!! > 0) {
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
                        val hours = duration!! / 60
                        val minutes = duration!! % 60
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

/**
 * Умное поле для ввода времени с правильным смещением курсора
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartTimeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    hasEmptyError: Boolean = false,
    hasValidationError: Boolean = false
) {
    val isError = hasEmptyError || hasValidationError

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = null,
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        visualTransformation = TimeAutoFormatTransformation(),
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isError) coralDark else MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = if (isError) coralDark else MaterialTheme.colorScheme.outline,
            errorBorderColor = coralDark,
            errorCursorColor = coralDark,
            errorTrailingIconColor = coralDark,
            errorLeadingIconColor = coralDark
        )
    )
}

/**
 * Визуальное преобразование с автодобавлением двоеточия и правильным смещением курсора
 */
class TimeAutoFormatTransformation : androidx.compose.ui.text.input.VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val original = text.text

        // Форматируем текст для отображения
        val formatted = when (original.length) {
            0 -> ""
            1, 2 -> original
            3 -> "${original.take(2)}:${original[2]}"
            4 -> "${original.take(2)}:${original.drop(2)}"
            else -> original.take(4).let {
                "${it.take(2)}:${it.drop(2)}"
            }
        }

        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(formatted),
            TimeOffsetMapping(original)
        )
    }

    private class TimeOffsetMapping(private val original: String) : androidx.compose.ui.text.input.OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            // original: "093" (3 символа)
            // transformed: "09:3" (4 символа, двоеточие на позиции 2)
            // Если курсор был в конце (offset = 3), он должен перейти в конец (position = 4)

            return when {
                offset <= 2 -> offset // Первые 2 символа без изменений
                else -> offset + 1 // После 2 символов добавляем +1 для двоеточия
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            // transformed: "09:3"
            // Если курсор после двоеточия (position = 3), он должен соответствовать original[2]

            return when {
                offset <= 2 -> offset // Первые 2 символа без изменений
                else -> offset - 1 // После двоеточия вычитаем 1
            }
        }
    }
}

/**
 * Валидация времени в формате HH:MM
 * Возвращает сообщение об ошибке или null если время валидно
 */
private fun validateTime(timeText: String): String? {
    if (timeText.isEmpty()) {
        return null // Пустое время допустимо
    }

    // Внутреннее хранилище только цифры, нужно форматировать для проверки
    val formattedTime = formatForValidation(timeText)

    // Проверяем формат HH:MM
    if (!formattedTime.contains(':')) {
        // Если введены только часы (например "09")
        if (formattedTime.length == 2 && formattedTime.all { it.isDigit() }) {
            val hours = formattedTime.toIntOrNull()
            if (hours == null || hours < 0 || hours > 23) {
                return "Hours must be 0-23"
            }
            return "Enter minutes"
        }
        return "Invalid format"
    }

    val parts = formattedTime.split(":")
    if (parts.size != 2) {
        return "Invalid format"
    }

    val hoursStr = parts[0]
    val minutesStr = parts[1]

    // Проверяем что часы и минуты - числа
    val hours = hoursStr.toIntOrNull()
    val minutes = minutesStr.toIntOrNull()

    if (hours == null) {
        return "Hours must be a number"
    }

    if (minutes == null) {
        return "Minutes must be a number"
    }

    // Проверяем диапазоны
    if (hours < 0 || hours > 23) {
        return "Hours must be 0-23"
    }

    if (minutes < 0 || minutes > 59) {
        return "Minutes must be 0-59"
    }

    // Проверяем что введены все 4 цифры
    if (hoursStr.length != 2 || minutesStr.length != 2) {
        return "Enter all 4 digits"
    }

    return null // Валидно
}

/**
 * Форматирует внутреннее значение (только цифры) в формат HH:MM для валидации
 */
private fun formatForValidation(digits: String): String {
    return when (digits.length) {
        0 -> ""
        1, 2 -> digits
        3 -> "${digits.take(2)}:${digits[2]}"
        4 -> "${digits.take(2)}:${digits.drop(2)}"
        else -> digits.take(4).let { "${it.take(2)}:${it.drop(2)}" }
    }
}

/**
 * Конвертирует текст времени в миллисекунды (относительно текущей даты)
 */
private fun convertTimeToMillis(timeText: String): Long {
    // timeText содержит только цифры, форматируем
    val formatted = formatForValidation(timeText)
    val parts = formatted.split(":")
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}

/**
 * Вычисляет продолжительность в минутах между двумя временами
 */
private fun calculateDuration(startTime: String, endTime: String): Int? {
    try {
        val startMillis = convertTimeToMillis(startTime)
        val endMillis = convertTimeToMillis(endTime)

        // Если endTime раньше startTime, предполагаем что это на следующий день
        val durationMillis = if (endMillis < startMillis) {
            endMillis + (24 * 60 * 60 * 1000) - startMillis // Добавляем 24 часа
        } else {
            endMillis - startMillis
        }

        return (durationMillis / (1000 * 60)).toInt()
    } catch (e: Exception) {
        return null
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
            val index = (text.hashCode() and Int.MAX_VALUE) % 20
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
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    VibeDoTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AddTaskScreen(
                viewModel = TaskViewModel(
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
                ),
                onNavigateBack = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTagDialogEmptyPreview() {
    VibeDoTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AddTagDialog(
                    tagName = "",
                    colorIndex = 0,
                    allColors = getAllAvailableColors(),
                    onTagNameChange = {},
                    onColorIndexChange = {},
                    onConfirm = {},
                    onDismiss = {}
                )
            }
        }
    }
}