package io.block.goose.sunday.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.Calendar

fun getDynamicGradientBrush(): Brush {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val minute = Calendar.getInstance().get(Calendar.MINUTE)
    val timeProgress = hour + minute / 60.0

    val (startColor, endColor) = when {
        timeProgress < 5 || timeProgress > 22 -> Color(0xFF0F1C3D) to Color(0xFF0A1228) // Night
        timeProgress < 6 -> Color(0xFF1E3A5F) to Color(0xFF2D4A7C)     // Pre-dawn
        timeProgress < 6.5 -> Color(0xFF3D5A80) to Color(0xFF5C7CAE)   // Early dawn
        timeProgress < 7 -> Color(0xFF5C7CAE) to Color(0xFFEE9B7A)     // Dawn
        timeProgress < 8 -> Color(0xFFF4A261) to Color(0xFF87CEEB)     // Sunrise
        timeProgress < 10 -> Color(0xFF5CA9D6) to Color(0xFF87CEEB)    // Morning
        timeProgress < 16 -> Color(0xFF4A90E2) to Color(0xFF7BB7E5)    // Midday
        timeProgress < 17 -> Color(0xFF5CA9D6) to Color(0xFF87B8D4)    // Late afternoon
        timeProgress < 18.5 -> Color(0xFFF4A261) to Color(0xFFE76F51)  // Golden hour
        timeProgress < 19.5 -> Color(0xFFE76F51) to Color(0xFFC44569)  // Sunset
        timeProgress < 20.5 -> Color(0xFFC44569) to Color(0xFF6A4C93)  // Late sunset
        else -> Color(0xFF6A4C93) to Color(0xFF1E3A5F)                 // Dusk
    }

    return Brush.linearGradient(
        colors = listOf(startColor, endColor)
    )
}
