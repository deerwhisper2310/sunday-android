package io.block.goose.sunday.domain.model

enum class SunscreenLevel(
    val description: String,
    val uvTransmissionFactor: Double
) {
    NONE("None", 1.0),
    SPF15("SPF 15", 0.07),
    SPF30("SPF 30", 0.03),
    SPF50("SPF 50", 0.02),
    SPF100("SPF 100+", 0.01);
}
