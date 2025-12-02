package com.example.vibedo.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

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
        CardColorPair(peach, peachDark, "Peach"),
        CardColorPair(coral, coralDark, "Coral"),
        CardColorPair(lemon, lemonDark, "Lemon"),
        CardColorPair(butter, butterDark, "Butter"),
        CardColorPair(mint, mintDark, "Mint"),
        CardColorPair(aqua, aquaDark, "Aqua"),
        CardColorPair(turquoise, turquoiseDark, "Turquoise"),
        CardColorPair(skyBlue, skyBlueDark, "Sky Blue"),
        CardColorPair(azure, azureDark, "Azure"),
        CardColorPair(lavender, lavenderDark, "Lavender"),
        CardColorPair(lilac, lilacDark, "Lilac"),
        CardColorPair(amethyst, amethystDark, "Amethyst"),
        CardColorPair(sage, sageDark, "Sage"),
        CardColorPair(olive, oliveDark, "Olive"),
        CardColorPair(moss, mossDark, "Moss"),
        CardColorPair(sand, sandDark, "Sand"),
        CardColorPair(cream, creamDark, "Cream"),
        CardColorPair(vanilla, vanillaDark, "Vanilla"),
        CardColorPair(pearl, pearlDark, "Pearl"),
        CardColorPair(silver, silverDark, "Silver")
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