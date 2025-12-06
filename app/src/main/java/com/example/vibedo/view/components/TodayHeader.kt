package com.example.vibedo.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibedo.view.theme.VibeDoTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodayHeader() {
    val currentDate = remember { Date() }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.ENGLISH) }
    val dayFormatter = remember { SimpleDateFormat("EEEE", Locale.ENGLISH) }
    val dateFormatter = remember { SimpleDateFormat("d", Locale.ENGLISH) }
    val monthFormatter = remember { SimpleDateFormat("MMMM", Locale.ENGLISH) }

    var currentTime by remember { mutableStateOf(timeFormatter.format(Date())) }

    // Обновляем время каждые 10 секунд
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000) // 10 секунд
            currentTime = timeFormatter.format(Date())
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom // Выравниваем по нижнему краю
    ) {
        // Левый столбец - день недели, число и месяц
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.Bottom, // Содержимое внизу
            horizontalAlignment = Alignment.Start
        ) {
            // День недели
            Text(
                text = dayFormatter.format(currentDate).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Число (очень большое)
            Text(
                text = dateFormatter.format(currentDate),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp, // Увеличили с 64 до 72
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(80.dp) // Увеличили высоту
            )

            // Месяц
            Text(
                text = monthFormatter.format(currentDate).uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 36.sp, // Увеличили с 32 до 36
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Вертикальная линия - от верхнего до нижнего края
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color.Gray.copy(alpha = 0.4f))
        )

        // Правый столбец - время и город (внизу колонки)
        Column(
            modifier = Modifier
                .width(160.dp) // Увеличили ширину
                .padding(start = 12.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom // Содержимое внизу
        ) {
            // Город (сверху)
            Text(
                text = "MOSCOW",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                maxLines = 1,
                modifier = Modifier.padding(bottom = 8.dp) // Отступ снизу перед временем
            )

            // Время (большое, внизу)
            Text(
                text = currentTime,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp, // Увеличили с 24 до 40
                    fontWeight = FontWeight.Bold // Сделали жирнее
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun TodayHeaderPreview() {
    VibeDoTheme(darkTheme = true) {
        Surface {
            TodayHeader()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TodayHeaderLightPreview() {
    VibeDoTheme(darkTheme = false) {
        Surface {
            TodayHeader()
        }
    }
}