package io.block.goose.sunday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.block.goose.sunday.ui.UiState
import io.block.goose.sunday.ui.UvDataState
import io.block.goose.sunday.domain.calculator.VitaminDCalculator
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun VitaminDInfoCard(
    uiState: UiState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 1f),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "How It Works",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    text = "Sun Day uses a scientifically-based multi-factor model to estimate vitamin D synthesis from UV exposure.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "The calculation considers UV intensity, time of day, clothing coverage, skin type, age, and recent exposure history.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "Base rate: 21,000 IU/hr (minimal clothing, ~80% exposure)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val uvDataState = uiState.uvDataState
                val currentUv: Double = if (uvDataState is UvDataState.Success) {
                    val now = ZonedDateTime.now()
                    val zoneId = ZoneId.of(uvDataState.uvResponse.timezone)
                    val currentTimeIndex = uvDataState.uvResponse.hourly.time.indexOfFirst {
                        LocalDateTime.parse(it).atZone(zoneId) > now
                    }
                    if (currentTimeIndex != -1) uvDataState.uvResponse.hourly.uvIndex[currentTimeIndex] ?: 0.0 else 0.0
                } else 0.0

                val vitaminDCalculator = VitaminDCalculator()
                val vitaminDPerMinute = vitaminDCalculator.calculateVitaminDRate(
                    uvIndex = currentUv,
                    clothingLevel = uiState.userPreferences.clothingLevel,
                    sunscreen = uiState.userPreferences.sunscreen,
                    skinType = uiState.userPreferences.skinType,
                    userAge = uiState.userPreferences.age, // Use user's age
                    currentTime = ZonedDateTime.now(), // Current time for UV quality factor
                    averageDailyExposure = 1000.0 // Placeholder as in MainViewModel
                ) / 60.0 // Convert IU/hour to IU/minute

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Current Factors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoText("UV Index", String.format("%.1f", currentUv))
                Text(
                    text = "Higher UV levels increase production, but the effect plateaus at high indexes.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoText("Skin Type", uiState.userPreferences.skinType.fitzpatrickName)
                Text(
                    text = "Lighter skin tones synthesize Vitamin D at a higher rate than darker tones.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoText("Clothing", uiState.userPreferences.clothingLevel.description)
                Text(
                    text = "The more skin covered, the lower the Vitamin D production.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoText("Sunscreen", uiState.userPreferences.sunscreen.displayName)
                Text(
                    text = "Sunscreen blocks UVB rays necessary for Vitamin D synthesis, significantly reducing production.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoText("Age", uiState.userPreferences.age.toString())
                Text(
                    text = "The body's ability to produce Vitamin D naturally declines with age (~1% decrease per year after 20, 25% efficiency at ≥70).",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(vertical = 20.dp))

                Text(
                    text = "Other Factors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "• UV Quality Factor (Time of Day): Accounts for solar angle effects; production is most efficient around solar noon.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "• Adaptation Factor: Based on recent exposure history. (Note: In this Android version, this factor uses a fixed placeholder value.)",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This calculation is an estimate based on these factors. Individual results may vary and this should not be considered medical advice.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Got It")
                }
            }
        }
    }
}

@Composable
fun InfoText(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
