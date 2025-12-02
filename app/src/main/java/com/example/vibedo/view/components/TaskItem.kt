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
                    TimeText(time = task.startTime, label = "Start", textColor = cardColors.contentColor)
                }

                DurationChip(duration = task.duration, cardColors = cardColors)

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    TimeText(time = task.endTime, label = "End", textColor = cardColors.contentColor)
                }
            }

//            // Кнопка удаления (маленькая в правом нижнем углу)
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.BottomEnd
//            ) {
//                IconButton(
//                    onClick = onDeleteClick,
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    Icon(
//                        Icons.Default.Delete,
//                        contentDescription = "Delete",
//                        tint = cardColors.contentColor.copy(alpha = 0.7f),
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
        }
    }
}

@Composable
fun TimeText(
    time: Long?,
    label: String,
    textColor: Color
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor.copy(alpha = 0.6f)
    )

    Spacer(modifier = Modifier.height(4.dp))

    if (time != null) {
        val formatter = remember { SimpleDateFormat("h:mm a", Locale.ENGLISH) }
        val timeText = formatter.format(Date(time))

        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = textColor
        )
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
fun TaskItemPreview() {
    VibeDoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                TaskItem(
                    task = TaskEntity(
                        id = index.toLong(),
                        title = "Task ${index + 1} now time for work harder to be better",
                        description = "Description for task ${index + 1}",
                        priority = index % 3,
                        duration = 30 + index * 15
                    ),
                    onDeleteClick = {}
                )
            }
        }
    }
}