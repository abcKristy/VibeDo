package com.example.vibedo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.view.theme.CardColorPair
import com.example.vibedo.view.theme.VibeDoTheme
import com.example.vibedo.view.theme.rememberCardColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItem(
    task: TaskEntity,
    onDeleteClick: () -> Unit,
    showTimeLabels: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cardColors = rememberCardColors(task.colorIndex)
    val priorityIcon = when (task.priority) {
        0 -> Icons.Default.LowPriority
        1 -> Icons.Default.PriorityHigh
        2 -> Icons.Default.Warning
        else -> Icons.Default.LowPriority
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColors.backgroundColor,
            contentColor = cardColors.contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f),
                    color = cardColors.contentColor
                )

                Spacer(Modifier.width(80.dp))

                Icon(
                    imageVector = priorityIcon,
                    contentDescription = "Priority",
                    tint = cardColors.contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }


            if (showTimeLabels) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (task.startTime != null) {
                            TimeText(time = task.startTime, label = "Start", textColor = cardColors.contentColor)
                        } else {
                            Box(modifier = Modifier.height(24.dp))
                        }
                    }

                    DurationChip(duration = task.duration, cardColors = cardColors)

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (task.endTime != null) {
                            TimeText(time = task.endTime, label = "End", textColor = cardColors.contentColor)
                        } else {
                            Box(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeText(
    time: Long?,
    label: String,
    textColor: Color
) {
    if (time != null) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            val formatter = remember { SimpleDateFormat("h:mm a", Locale.ENGLISH) }
            val timeText = formatter.format(Date(time))

            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = textColor
            )
        }
    } else {
        Box(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DurationChip(
    duration: Int?,
    cardColors: CardColorPair
) {
    if (duration != null && duration > 0) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = cardColors.contentColor.copy(alpha = 0.1f),
            contentColor = cardColors.contentColor
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
    } else {
        Box(modifier = Modifier.width(60.dp).height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemWithTimePreview() {
    VibeDoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskItem(
                task = TaskEntity(
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
                onDeleteClick = {},
                showTimeLabels = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemWithoutTimePreview() {
    VibeDoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskItem(
                task = TaskEntity(
                    id = 2,
                    title = "Read a book",
                    description = "Read 50 pages",
                    priority = 1,
                    tag = "personal",
                    colorIndex = 2,
                    startTime = null,
                    endTime = null,
                    duration = null
                ),
                onDeleteClick = {},
                showTimeLabels = false
            )
        }
    }
}