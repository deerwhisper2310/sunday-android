package io.block.goose.sunday.domain.model

enum class Sunscreen(val spf: Int, val uvTransmissionFactor: Double) {
    NONE(0, 1.0),
    SPF_15(15, 0.07),
    SPF_30(30, 0.03),
    SPF_50(50, 0.02);

    val displayName: String
        get() = when (this) {
            NONE -> "None"
            SPF_15 -> "SPF 15"
            SPF_30 -> "SPF 30"
            SPF_50 -> "SPF 50"
        }
}