package com.example.vibedo.view.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.sp
import com.example.vibedo.model.ITaskRepository
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.model.TaskTag
import com.example.vibedo.model.TaskTagDao
import com.example.vibedo.view.theme.CardColorManager
import com.example.vibedo.view.theme.CardColorPair
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.view.theme.coralDark
import com.example.vibedo.view.theme.cream
import com.example.vibedo.view.theme.creamDark
import com.example.vibedo.view.theme.getAllAvailableColors
import com.example.vibedo.view.theme.olive
import com.example.vibedo.view.theme.oliveDark
import com.example.vibedo.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
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
    var priority by remember { mutableIntStateOf(0) }
    var tag by remember { mutableStateOf("task") }

    // Режим: Task (задача) или Event (событие)
    var isEventMode by remember { mutableStateOf(true) }

    // Для Event режима: даты начала и окончания
    var startDate by remember { mutableStateOf(Calendar.getInstance()) }
    var endDate by remember { mutableStateOf(Calendar.getInstance()) }
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    // Для Task режима: дедлайн
    var deadlineDate by remember { mutableStateOf(Calendar.getInstance()) }
    var deadlineTimeText by remember { mutableStateOf("") }

    // Ошибки валидации
    var titleError by remember { mutableStateOf(false) }
    var startTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }
    var deadlineTimeError by remember { mutableStateOf<String?>(null) }

    // Для расчета продолжительности (только для Event)
    var duration by remember { mutableStateOf<Int?>(null) }

    // Цвет
    var colorIndex by remember { mutableIntStateOf(0) }
    val allColors = getAllAvailableColors()

    // Для добавления нового тега
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColorIndex by remember { mutableIntStateOf(0) }

    // Диалоги выбора даты
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showDeadlineDatePicker by remember { mutableStateOf(false) }

    // Загружаем пользовательские теги
    val customTags by viewModel.customTags.collectAsState(initial = emptyList())

    // Все теги: предопределенные + пользовательские
    val allTags = remember(customTags) {
        CardColorManager.predefinedTags + customTags.map { it.name }
    }

    LaunchedEffect(tag) {
        if (CardColorManager.isPredefinedTag(tag)) {
            val defaultColor = CardColorManager.getDefaultForTag(tag)
            colorIndex = CardColorManager.getColorIndex(defaultColor)
        } else {
            val customTag = customTags.find { it.name == tag }
            customTag?.let {
                colorIndex = it.colorIndex
            }
        }
    }

    LaunchedEffect(startTimeText, endTimeText, startDate, endDate) {
        if (isEventMode) {
            startTimeError = validateTime(startTimeText)
            endTimeError = validateTime(endTimeText)

            if (startTimeError == null && endTimeError == null &&
                startTimeText.isNotEmpty() && endTimeText.isNotEmpty()) {
                duration = calculateDuration(startTimeText, endTimeText, startDate, endDate)
            } else {
                duration = null
            }
        }
    }

    LaunchedEffect(deadlineTimeText) {
        if (!isEventMode) {
            deadlineTimeError = validateTime(deadlineTimeText)
        }
    }

    val showColorPalette = tag == "task"

    fun isEndDateBeforeStartDate(): Boolean {
        if (!isEventMode) return false

        val start = startDate.timeInMillis
        val end = endDate.timeInMillis

        val startCal = Calendar.getInstance().apply {
            timeInMillis = start
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = Calendar.getInstance().apply {
            timeInMillis = end
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return endCal.before(startCal)
    }

    fun validateAndSave() {
        titleError = false

        var hasError = false

        if (title.isBlank()) {
            titleError = true
            hasError = true
        }

        if (isEventMode) {
            if (startTimeError != null || endTimeError != null) {
                hasError = true
            }

            if (isEndDateBeforeStartDate()) {
                endTimeError = "End date cannot be before start date"
                hasError = true
            }

            if (endTimeText.isNotEmpty() && startTimeText.isEmpty()) {
                startTimeError = "Specify start time if end time is set"
                hasError = true
            }
        } else {
            if (deadlineTimeText.isNotEmpty() && deadlineTimeError != null) {
                hasError = true
            }
        }

        if (hasError) {
            val errorMessage = if (titleError) {
                "Fill in the task title"
            } else if (isEventMode && (startTimeError != null || endTimeError != null)) {
                "Check time fields"
            } else if (!isEventMode && deadlineTimeError != null) {
                "Check deadline time"
            } else {
                "Fill in required fields"
            }

            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            return
        }

        if (isEventMode) {
            val startTimeMillis = if (startTimeText.isNotEmpty()) {
                convertTimeToMillis(startTimeText, startDate)
            } else null

            val endTimeMillis = if (endTimeText.isNotEmpty()) {
                convertTimeToMillis(endTimeText, endDate)
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
        } else {
            val deadlineMillis = if (deadlineTimeText.isNotEmpty()) {
                convertTimeToMillis(deadlineTimeText, deadlineDate)
            } else null

            viewModel.addTask(
                title = title,
                description = description,
                priority = priority,
                tag = tag,
                startTime = null,
                endTime = deadlineMillis,
                duration = null,
                colorIndex = colorIndex
            )
        }

        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            color = if (isEventMode) olive else cream,
                            border = BorderStroke(
                                2.dp,
                                if (isEventMode) oliveDark.copy(0.5f) else creamDark.copy(0.5f)
                            ),
                            modifier = Modifier
                                .width(120.dp)
                                .height(32.dp)
                                .clickable { isEventMode = !isEventMode }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEventMode) "Event Mode" else "Task Mode",
                                    color = if (isEventMode) oliveDark else creamDark,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                },
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
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Title*") },
                placeholder = { Text("Enter title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                isError = titleError,
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

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("Enter description") },
                singleLine = false,
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isEventMode) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartDatePicker = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Select start date",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatDateForDisplay(startDate.time),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.width(120.dp)) {
                        CompactTimeInputField(
                            value = startTimeText,
                            onValueChange = { startTimeText = it },
                            placeholder = "09:00",
                            modifier = Modifier.fillMaxWidth(),
                            hasValidationError = startTimeError != null
                        )

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
                }
                Text(
                    text = "End",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndDatePicker = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Select end date",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatDateForDisplay(endDate.time),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.width(120.dp)) {
                        CompactTimeInputField(
                            value = endTimeText,
                            onValueChange = { endTimeText = it },
                            placeholder = "10:00",
                            modifier = Modifier.fillMaxWidth(),
                            hasValidationError = endTimeError != null
                        )

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
                    }
                }

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
            } else {
                Text(
                    text = "Deadline",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDeadlineDatePicker = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "Select deadline date",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatDateForDisplay(deadlineDate.time),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.width(120.dp)) {
                        CompactTimeInputField(
                            value = deadlineTimeText,
                            onValueChange = { deadlineTimeText = it },
                            placeholder = "18:00",
                            modifier = Modifier.fillMaxWidth(),
                            hasValidationError = deadlineTimeError != null
                        )

                        deadlineTimeError?.let { error ->
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
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

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

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(allTags) { currentTag ->
                    TagChip(
                        text = currentTag.replaceFirstChar { it.uppercase() },
                        selected = tag == currentTag,
                        onClick = { tag = currentTag }
                    )
                }
            }

            if (showColorPalette) {
                Text(
                    text = "Card Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(bottom = 8.dp)
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

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            startDate = calendar
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            endDate = calendar
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    if (showDeadlineDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineDate.timeInMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDeadlineDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            deadlineDate = calendar
                        }
                        showDeadlineDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeadlineDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTimeInputField(
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
        onValueChange = { newText ->
            val digitsOnly = newText.filter { it.isDigit() }
            if (digitsOnly.length <= 4) {
                onValueChange(digitsOnly)
            } else {
                onValueChange(digitsOnly.take(4))
            }
        },
        label = null,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        singleLine = true,
        modifier = modifier
            .height(48.dp)
            .padding(vertical = 0.dp),
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
            errorLeadingIconColor = coralDark,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = if (value.isNotEmpty()) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
/**
 * Визуальное преобразование с автодобавлением двоеточия и правильным смещением курсора
 */
class TimeAutoFormatTransformation : androidx.compose.ui.text.input.VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val original = text.text

        val digitsOnly = original.filter { it.isDigit() }
        val limitedDigits = if (digitsOnly.length > 4) digitsOnly.take(4) else digitsOnly

        val formatted = when (limitedDigits.length) {
            0 -> ""
            1, 2 -> limitedDigits
            3 -> "${limitedDigits.take(2)}:${limitedDigits[2]}"
            4 -> "${limitedDigits.take(2)}:${limitedDigits.drop(2)}"
            else -> "${limitedDigits.take(2)}:${limitedDigits.drop(2)}"
        }

        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(formatted),
            TimeOffsetMapping(limitedDigits)
        )
    }

    private class TimeOffsetMapping(private val original: String) : androidx.compose.ui.text.input.OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val actualOffset = if (offset > original.length) original.length else offset

            return when {
                actualOffset <= 2 -> actualOffset
                else -> actualOffset + 1
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            val adjustedOffset = if (offset < 0) 0 else offset

            return when {
                adjustedOffset <= 2 -> adjustedOffset
                else -> adjustedOffset - 1
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
        return null
    }

    val digitsOnly = timeText.filter { it.isDigit() }
    if (digitsOnly.length < 4) {
        return "Enter all 4 digits"
    }

    val formattedTime = "${digitsOnly.take(2)}:${digitsOnly.drop(2)}"

    val parts = formattedTime.split(":")
    if (parts.size != 2) {
        return "Invalid format"
    }

    val hoursStr = parts[0]
    val minutesStr = parts[1]

    val hours = hoursStr.toIntOrNull()
    val minutes = minutesStr.toIntOrNull()

    if (hours == null) {
        return "Hours must be a number"
    }

    if (minutes == null) {
        return "Minutes must be a number"
    }

    if (hours < 0 || hours > 23) {
        return "Hours must be 0-23"
    }

    if (minutes < 0 || minutes > 59) {
        return "Minutes must be 0-59"
    }

    return null
}

/**
 * Фильтр для ограничения ввода только цифрами и не более 4 символов
 */
fun filterTimeInput(text: String, newChar: Char): String {
    if (!newChar.isDigit()) {
        return text
    }

    val currentDigits = text.filter { it.isDigit() }

    if (currentDigits.length >= 4) {
        return text
    }

    return text + newChar
}

/**
 * Форматирует внутреннее значение (только цифры) в формат HH:MM для валидации
 */
private fun formatForValidation(digits: String): String {
    val limitedDigits = if (digits.length > 4) digits.take(4) else digits

    return when (limitedDigits.length) {
        0 -> ""
        1, 2 -> limitedDigits
        3 -> "${limitedDigits.take(2)}:${limitedDigits[2]}"
        4 -> "${limitedDigits.take(2)}:${limitedDigits.drop(2)}"
        else -> limitedDigits.take(4).let { "${it.take(2)}:${it.drop(2)}" }
    }
}

/**
 * Конвертирует текст времени в миллисекунды с учетом выбранной даты
 */
private fun convertTimeToMillis(timeText: String, date: Calendar): Long {
    val formatted = formatForValidation(timeText)
    val parts = formatted.split(":")
    val hours = parts[0].toInt()
    val minutes = parts[1].toInt()

    val calendar = Calendar.getInstance()
    calendar.time = date.time
    calendar.set(Calendar.HOUR_OF_DAY, hours)
    calendar.set(Calendar.MINUTE, minutes)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}

/**
 * Вычисляет продолжительность в минутах между двумя временами с учетом дат
 */
private fun calculateDuration(
    startTime: String,
    endTime: String,
    startDate: Calendar,
    endDate: Calendar
): Int? {
    try {
        val startMillis = convertTimeToMillis(startTime, startDate)
        val endMillis = convertTimeToMillis(endTime, endDate)

        val durationMillis = endMillis - startMillis

        if (durationMillis < 0) {
            return null
        }

        return (durationMillis / (1000 * 60)).toInt()
    } catch (e: Exception) {
        return null
    }
}

/**
 * Форматирует дату для отображения
 */
private fun formatDateForDisplay(date: Date): String {
    val dateFormatter = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
    return dateFormatter.format(date)
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(80.dp)
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
    colorPair: CardColorPair,
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

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (!isSelected) {
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
    val backgroundColor = when (text.lowercase()) {
        "task" -> Color(0xFFE8F5E8)
        "meeting" -> Color(0xFFE3F2FD)
        "lesson" -> Color(0xFFF3E5F5)
        "work" -> Color(0xFFFFF3E0)
        "personal" -> Color(0xFFFCE4EC)
        "urgent" -> Color(0xFFFFEBEE)
        else -> {
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