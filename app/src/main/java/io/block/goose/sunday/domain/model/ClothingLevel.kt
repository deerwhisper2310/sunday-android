package io.block.goose.sunday.domain.model

enum class ClothingLevel(
    val description: String,
    val shortDescription: String,
    val exposureFactor: Double
) {
    MINIMAL("Minimal (swimwear)", "Minimal", 0.80),
    LIGHT("Light (shorts, tee)", "Light", 0.50),
    MODERATE("Moderate (pants, tee)", "Moderate", 0.30),
    HEAVY("Heavy (pants, sleeves)", "Heavy", 0.10);

    val displayName: String
        get() = description
}
