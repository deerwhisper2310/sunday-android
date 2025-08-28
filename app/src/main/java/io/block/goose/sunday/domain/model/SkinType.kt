package io.block.goose.sunday.domain.model

import androidx.compose.ui.graphics.Color

enum class SkinType(
    val numericValue: Int, // New: Arabic numeral (1-6)
    val fitzpatrickName: String, // New: Fitzpatrick name
    val description: String,
    val vitaminDFactor: Double,
    val color: Color
) {
    TYPE1(1, "Very Fair", "Always burns, never tans", 1.25, Color(0xFFF7E8D6)),
    TYPE2(2, "Fair", "Usually burns, tans minimally", 1.1, Color(0xFFF2D8B9)),
    TYPE3(3, "Light", "Sometimes burns, tans uniformly", 1.0, Color(0xFFE6C4A9)),
    TYPE4(4, "Medium", "Burns minimally, tans well", 0.7, Color(0xFFD1A888)),
    TYPE5(5, "Dark", "Rarely burns, tans profusely", 0.4, Color(0xFFA17C64)),
    TYPE6(6, "Very Dark", "Never burns, deeply pigmented", 0.2, Color(0xFF664B3C));

    // Update displayName to now return "Type 1", "Type 2", etc.
    // This will affect wherever displayName is used on the main screen.
    val displayName: String
        get() = "Type $numericValue"
}