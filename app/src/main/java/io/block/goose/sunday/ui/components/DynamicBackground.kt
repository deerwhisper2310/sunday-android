package io.block.goose.sunday.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    currentTime: ZonedDateTime,
    content: @Composable () -> Unit
) {
    val gradient = when (currentTime.hour) {
        in 5..7 -> sunriseGradient // Sunrise
        in 8..17 -> dayGradient     // Day
        in 18..20 -> sunsetGradient // Sunset
        else -> nightGradient       // Night
    }

    Box(
        modifier = modifier.background(brush = Brush.verticalGradient(gradient))
    ) {
        content()
    }
}

private val dayGradient = listOf(Color(0xFF87CEEB), Color(0xFF00BFFF))
private val nightGradient = listOf(Color(0xFF00008B), Color(0xFF191970))
private val sunriseGradient = listOf(Color(0xFFFF7F50), Color(0xFF8A2BE2))
private val sunsetGradient = listOf(Color(0xFF4B0082), Color(0xFF8B008B))
