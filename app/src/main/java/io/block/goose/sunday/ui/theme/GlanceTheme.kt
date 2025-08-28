package io.block.goose.sunday.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.glance.material3.ColorProviders

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

object SundayGlanceColorScheme {
    val colors = ColorProviders(
        light = LightColorScheme,
        dark = DarkColorScheme
    )
}
