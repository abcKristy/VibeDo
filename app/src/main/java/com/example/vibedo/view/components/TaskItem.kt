package com.example.vibedo.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vibedo.model.TaskEntity
import com.example.vibedo.view.theme.VibeDoTheme

@Composable
fun TaskItem(
    task: TaskEntity,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Чекбокс и текст
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )

                    task.description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Приоритет
                    Spacer(modifier = Modifier.height(4.dp))
                    PriorityIndicator(priority = task.priority)
                }
            }

            // Кнопка удаления
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PriorityIndicator(priority: Int) {
    val (text, color) = when (priority) {
        0 -> Pair("Low", Color.Green)
        1 -> Pair("Medium", Color.Yellow)
        2 -> Pair("High", Color.Red)
        else -> Pair("Low", Color.Gray)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .wrapContentSize(),
        color = color.copy(alpha = 0.2f),
        contentColor = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    VibeDoTheme {
        Surface {
            TaskItem(
                task = TaskEntity(
                    id = 1,
                    title = "Complete UI design",
                    description = "Finish all screens and components",
                    isCompleted = false,
                    priority = 2,
                    createdDate = System.currentTimeMillis()
                ),
                onCheckedChange = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemCompletedPreview() {
    VibeDoTheme {
        Surface {
            TaskItem(
                task = TaskEntity(
                    id = 2,
                    title = "Buy groceries",
                    description = "Milk, eggs, bread",
                    isCompleted = true,
                    priority = 0,
                    createdDate = System.currentTimeMillis()
                ),
                onCheckedChange = {},
                onDeleteClick = {}
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PriorityIndicatorPreview() {
    VibeDoTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            PriorityIndicator(priority = 0)
            PriorityIndicator(priority = 1)
            PriorityIndicator(priority = 2)
        }
    }
}