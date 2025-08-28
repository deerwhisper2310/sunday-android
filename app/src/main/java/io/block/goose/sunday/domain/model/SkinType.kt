package io.block.goose.sunday.domain.model

import androidx.compose.ui.graphics.Color

enum class SkinType(
    val description: String,
    val vitaminDFactor: Double,
    val color: Color
) {
    TYPE1("Always burns, never tans", 1.25, Color(0xFFF7E8D6)),
    TYPE2("Usually burns, tans minimally", 1.1, Color(0xFFF2D8B9)),
    TYPE3("Sometimes burns, tans uniformly", 1.0, Color(0xFFE6C4A9)),
    TYPE4("Burns minimally, tans well", 0.7, Color(0xFFD1A888)),
    TYPE5("Rarely burns, tans profusely", 0.4, Color(0xFFA17C64)),
    TYPE6("Never burns, deeply pigmented", 0.2, Color(0xFF664B3C));

    val displayName: String
        get() {
            val roman = when (this) {
                TYPE1 -> "I"
                TYPE2 -> "II"
                TYPE3 -> "III"
                TYPE4 -> "IV"
                TYPE5 -> "V"
                TYPE6 -> "VI"
            }
            return "Type $roman"
        }
}
