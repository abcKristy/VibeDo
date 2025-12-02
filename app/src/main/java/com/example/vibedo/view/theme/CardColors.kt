package com.example.vibedo.view.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

data class CardColorPair(
    val backgroundColor: Color,
    val contentColor: Color
)

object CardColorManager {
    private val colorPairs = listOf(
        // 1. Персиковый
        CardColorPair(peach, peachDark),
        // 2. Мятный
        CardColorPair(mint, mintDark),
        // 3. Лавандовый
        CardColorPair(lavender, lavenderDark),
        // 4. Песочный
        CardColorPair(sand, sandDark),
        // 5. Нежно-голубой
        CardColorPair(skyBlue, skyBlueDark),
        // 6. Оливковый
        CardColorPair(olive, oliveDark),
        // 7. Коралловый
        CardColorPair(coral, coralDark),
        // 8. Сливочный
        CardColorPair(cream, creamDark),
        // 9. Аквамариновый
        CardColorPair(aqua, aquaDark),
        // 10. Сиреневый
        CardColorPair(lilac, lilacDark),
        // 11. Лимонный
        CardColorPair(lemon, lemonDark),
        // 12. Пыльная роза
        CardColorPair(dustyRose, dustyRoseDark),
        // 13. Шалфейный
        CardColorPair(sage, sageDark),
        // 14. Перламутровый
        CardColorPair(pearl, pearlDark),
        // 15. Бирюзовый
        CardColorPair(turquoise, turquoiseDark),
        // 16. Ванильный
        CardColorPair(vanilla, vanillaDark),
        // 17. Глиняный
        CardColorPair(clay, clayDark),
        // 18. Серебряный
        CardColorPair(silver, silverDark),
        // 19. Морской волны
        CardColorPair(seaWave, seaWaveDark),
        // 20. Фисташковый
        CardColorPair(pistachio, pistachioDark)
    )

    private var currentIndex = 0

    fun getNextColor(): CardColorPair {
        val color = colorPairs[currentIndex]
        currentIndex = (currentIndex + 1) % colorPairs.size
        return color
    }


    fun getColorByIndex(index: Int): CardColorPair {
        return colorPairs[index % colorPairs.size]
    }


    fun getColorIndex(pair: CardColorPair): Int {
        return colorPairs.indexOfFirst {
            it.backgroundColor == pair.backgroundColor &&
                    it.contentColor == pair.contentColor
        }.takeIf { it != -1 } ?: 0
    }


    fun reset() {
        currentIndex = 0
    }

    fun getAllColors(): List<CardColorPair> = colorPairs
}


@Composable
fun rememberCardColors(cardId: Long): CardColorPair {
    return remember(cardId) {
        val index = (cardId % CardColorManager.getAllColors().size).toInt()
        CardColorManager.getColorByIndex(index)
    }
}


@Composable
fun rememberNextCardColor(): CardColorPair {
    return remember { CardColorManager.getNextColor() }
}