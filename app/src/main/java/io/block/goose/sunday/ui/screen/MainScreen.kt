package io.block.goose.sunday.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.block.goose.sunday.domain.model.ClothingLevel
import io.block.goose.sunday.domain.model.SkinType
import io.block.goose.sunday.ui.UiEvent
import io.block.goose.sunday.ui.UiState
import io.block.goose.sunday.ui.UvDataState
import io.block.goose.sunday.ui.components.DynamicBackground
import io.block.goose.sunday.ui.components.InfoCard
import io.block.goose.sunday.ui.components.SettingsPicker
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun MainScreen(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit
) {
    DynamicBackground(currentTime = ZonedDateTime.now()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val uvDataState = uiState.uvDataState) {
                is UvDataState.Loading -> {
                    CircularProgressIndicator()
                }
                is UvDataState.Success -> {
                    val now = ZonedDateTime.now()
                    val zoneId = ZoneId.of(uvDataState.uvResponse.timezone)
                    val currentTimeIndex = uvDataState.uvResponse.hourly.time.indexOfFirst {
                        LocalDateTime.parse(it).atZone(zoneId) > now
                    }
                    val currentUv = if (currentTimeIndex != -1) uvDataState.uvResponse.hourly.uvIndex[currentTimeIndex] else 0.0

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        InfoCard(
                            title = "Current UV Index",
                            value = String.format("%.1f", currentUv)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoCard(
                            title = "Vitamin D (IU/hr)",
                            value = String.format("%.0f", uiState.vitaminDRate)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        SettingsPicker(
                            title = "Skin Type",
                            selectedValue = uiState.userPreferences.skinType,
                            options = SkinType.values().toList(),
                            onValueChange = { onEvent(UiEvent.SkinTypeChanged(it)) },
                            displayTransform = { it.displayName }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SettingsPicker(
                            title = "Clothing",
                            selectedValue = uiState.userPreferences.clothingLevel,
                            options = ClothingLevel.values().toList(),
                            onValueChange = { onEvent(UiEvent.ClothingChanged(it)) },
                            displayTransform = { it.displayName }
                        )
                    }
                }
                is UvDataState.Error -> {
                    Text(
                        text = "Error: ${uvDataState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}