package io.block.goose.sunday.domain.model

enum class SkinType(
    val description: String,
    val vitaminDFactor: Double
) {
    TYPE1("Very fair", 1.25),
    TYPE2("Fair", 1.1),
    TYPE3("Light", 1.0),
    TYPE4("Medium", 0.7),
    TYPE5("Dark", 0.4),
    TYPE6("Very dark", 0.2);

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
            return "Type $roman: $description"
        }
}
