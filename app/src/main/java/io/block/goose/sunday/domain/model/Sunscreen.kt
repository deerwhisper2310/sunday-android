package io.block.goose.sunday.domain.model

enum class Sunscreen(val spf: Int, val uvTransmissionFactor: Double, val description: String) {
    NONE(0, 1.0, "100% UV passes through"),
    SPF_15(15, 0.07, "Blocks ~93% of UV rays"),
    SPF_30(30, 0.03, "Blocks ~97% of UV rays"),
    SPF_50(50, 0.02, "Blocks ~98% of UV rays");

    val displayName: String
        get() = when (this) {
            NONE -> "None"
            SPF_15 -> "SPF 15"
            SPF_30 -> "SPF 30"
            SPF_50 -> "SPF 50"
        }
}