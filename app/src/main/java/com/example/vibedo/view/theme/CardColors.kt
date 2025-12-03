package com.example.vibedo.view.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vibedo.view.screens.CompactColorOption

/**
 * Пара цветов для карточки: светлый фон и темный текст/иконки
 */
data class CardColorPair(
    val backgroundColor: Color,
    val contentColor: Color,
    val name: String = ""
)

/**
 * Менеджер цветов карточек
 */
object CardColorManager {
    val colorPairs = listOf(
        // Группа 1: Синие и голубые тона
        CardColorPair(skyBlue, skyBlueDark, "Sky Blue"),
        CardColorPair(azure, azureDark, "Azure"),
        CardColorPair(seaWave, seaWaveDark, "Sea Wave"),
        CardColorPair(tealBlue, tealBlueDark, "Teal Blue"),
        CardColorPair(dustyBlue, dustyBlueDark, "Dusty Blue"),
        CardColorPair(slate, slateDark, "Slate"),
        CardColorPair(indigo, indigoDark, "Indigo"),
        CardColorPair(aqua, aquaDark, "Aqua"),
        CardColorPair(turquoise, turquoiseDark, "Turquoise"),

        // Группа 2: Зеленые тона
        CardColorPair(mint, mintDark, "Mint"),
        CardColorPair(emerald, emeraldDark, "Emerald"),
        CardColorPair(seafoam, seafoamDark, "Seafoam"),
        CardColorPair(sage, sageDark, "Sage"),
        CardColorPair(olive, oliveDark, "Olive"),
        CardColorPair(moss, mossDark, "Moss"),
        CardColorPair(pistachio, pistachioDark, "Pistachio"),

        // Группа 3: Фиолетовые и лиловые тона
        CardColorPair(lavender, lavenderDark, "Lavender"),
        CardColorPair(lilac, lilacDark, "Lilac"),
        CardColorPair(amethyst, amethystDark, "Amethyst"),
        CardColorPair(eggplant, eggplantDark, "Eggplant"),

        // Группа 4: Розовые, красные и коралловые тона
        CardColorPair(peach, peachDark, "Peach"),
        CardColorPair(coral, coralDark, "Coral"),
        CardColorPair(raspberry, raspberryDark, "Raspberry"),
        CardColorPair(fuchsia, fuchsiaDark, "Fuchsia"),
        CardColorPair(burgundyLight, burgundyDark, "Burgundy"),
        CardColorPair(teaRose, teaRoseDark, "Tea Rose"),
        CardColorPair(peachPink, peachPinkDark, "Peach Pink"),
        CardColorPair(vermilion, vermilionDark, "Vermilion"),

        // Группа 5: Желтые и оранжевые тона
        CardColorPair(lemon, lemonDark, "Lemon"),
        CardColorPair(butter, butterDark, "Butter"),
        CardColorPair(amber, amberDark, "Amber"),

        // Группа 6: Нейтральные и землистые тона
        CardColorPair(sand, sandDark, "Sand"),
        CardColorPair(cream, creamDark, "Cream"),
        CardColorPair(vanilla, vanillaDark, "Vanilla"),
        CardColorPair(pearl, pearlDark, "Pearl"),
        CardColorPair(khaki, khakiDark, "Khaki"),
        CardColorPair(terracotta, terracottaDark, "Terracotta"),
        CardColorPair(clay, clayDark, "Clay")
    )

    // Предопределенные теги (не пользовательские)
    val predefinedTags = listOf(
        "task", "meeting", "lesson", "work", "personal", "urgent"
    )

    // Цвета по умолчанию для предопределенных тегов
    val defaultTagColors = mapOf(
        "task" to colorPairs[0],       // Peach
        "meeting" to colorPairs[4],    // Mint
        "lesson" to colorPairs[10],    // Lilac
        "work" to colorPairs[2],       // Lemon
        "personal" to colorPairs[17],  // Vanilla
        "urgent" to colorPairs[1]      // Coral
    )

    /**
     * Получить цвет по индексу
     */
    fun getColorByIndex(index: Int): CardColorPair {
        return colorPairs[index % colorPairs.size]
    }

    /**
     * Получить индекс цвета
     */
    fun getColorIndex(pair: CardColorPair): Int {
        return colorPairs.indexOfFirst {
            it.backgroundColor == pair.backgroundColor &&
                    it.contentColor == pair.contentColor
        }.takeIf { it != -1 } ?: 0
    }

    /**
     * Получить цвет по умолчанию для тега
     */
    fun getDefaultForTag(tag: String): CardColorPair {
        return defaultTagColors[tag] ?: colorPairs[0]
    }

    /**
     * Проверить, является ли тег предопределенным
     */
    fun isPredefinedTag(tag: String): Boolean {
        return predefinedTags.contains(tag)
    }
}

/**
 * Композируемая функция для получения цветов карточки
 */
@Composable
fun rememberCardColors(colorIndex: Int): CardColorPair {
    return remember(colorIndex) {
        CardColorManager.getColorByIndex(colorIndex)
    }
}

/**
 * Получить все доступные цвета
 */
@Composable
fun getAllAvailableColors(): List<CardColorPair> {
    return remember { CardColorManager.colorPairs }
}


@Preview(showBackground = true)
@Composable
fun CardColorManagerPreview() {
    VibeDoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Демонстрация цветов карточек
            Text(
                text = "Card Color System Preview",
                style = MaterialTheme.typography.headlineMedium
            )

            // Показываем несколько цветов
            val colorsToShow = listOf(0, 5, 10, 15, 20, 25) // Выбираем разные индексы
            colorsToShow.forEach { index ->
                if (index < CardColorManager.colorPairs.size) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardColorManager.getColorByIndex(index).backgroundColor,
                            contentColor = CardColorManager.getColorByIndex(index).contentColor
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Color ${index + 1}: ${CardColorManager.getColorByIndex(index).name}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Text(
                text = "Predefined Tags",
                style = MaterialTheme.typography.titleMedium
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CardColorManager.predefinedTags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CardColorManager.getDefaultForTag(tag).backgroundColor,
                        contentColor = CardColorManager.getDefaultForTag(tag).contentColor
                    ) {
                        Text(
                            text = tag.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}