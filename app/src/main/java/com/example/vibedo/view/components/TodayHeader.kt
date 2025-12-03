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
    val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.ENGLISH) }
    val dayFormatter = remember { SimpleDateFormat("EEEE", Locale.ENGLISH) }
    val dateFormatter = remember { SimpleDateFormat("d", Locale.ENGLISH) }
    val monthFormatter = remember { SimpleDateFormat("MMMM", Locale.ENGLISH) }

    var currentTime by remember { mutableStateOf(timeFormatter.format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            currentTime = timeFormatter.format(Date())
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Левый столбец - день недели, число и месяц в столбце
        Column(
            modifier = Modifier.wrapContentWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            // День недели (меньше и не такой яркий)
            Text(
                text = dayFormatter.format(currentDate).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Число (очень большое и жирное)
            Text(
                text = dateFormatter.format(currentDate),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(72.dp)
            )

            // Месяц (большой и жирный, но меньше числа)
            Text(
                text = monthFormatter.format(currentDate).uppercase(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.width(50.dp))

        Box(
            modifier = Modifier
                .width(3.dp)
                .height(150.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(horizontal = 16.dp)
        )

        // Правый столбец - время
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "MOSCOW",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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