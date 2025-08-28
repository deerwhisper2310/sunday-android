package io.block.goose.sunday.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.block.goose.sunday.ui.UiEvent
import io.block.goose.sunday.ui.UiState
import io.block.goose.sunday.ui.UvDataState
import io.block.goose.sunday.ui.components.ClothingPicker
import io.block.goose.sunday.ui.components.DynamicBackground
import io.block.goose.sunday.ui.components.SkinTypePicker
import io.block.goose.sunday.ui.components.SunscreenPicker
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

private enum class ActiveSheet {
    NONE, SKIN_TYPE, CLOTHING, SUNSCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }

    fun hideBottomSheet() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                activeSheet = ActiveSheet.NONE
            }
        }
    }

    DynamicBackground {
        when (val uvDataState = uiState.uvDataState) {
            is UvDataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UvDataState.Success -> {
                val now = ZonedDateTime.now()
                val zoneId = ZoneId.of(uvDataState.uvResponse.timezone)
                val currentTimeIndex = uvDataState.uvResponse.hourly.time.indexOfFirst {
                    LocalDateTime.parse(it).atZone(zoneId) > now
                }
                val currentUv = if (currentTimeIndex != -1) uvDataState.uvResponse.hourly.uvIndex[currentTimeIndex] ?: 0.0 else 0.0

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Header()
                    Spacer(modifier = Modifier.height(20.dp))
                    UvSection(currentUv = currentUv)
                    Spacer(modifier = Modifier.height(20.dp))
                    VitaminDSection(vitaminDRate = uiState.vitaminDRate)
                    Spacer(modifier = Modifier.height(32.dp))
                    SettingsSection(
                        uiState = uiState,
                        onSkinTypeClick = { activeSheet = ActiveSheet.SKIN_TYPE },
                        onClothingClick = { activeSheet = ActiveSheet.CLOTHING },
                        onSunscreenClick = { activeSheet = ActiveSheet.SUNSCREEN }
                    )
                }
            }
            is UvDataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${uvDataState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (activeSheet != ActiveSheet.NONE) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = ActiveSheet.NONE },
                sheetState = sheetState
            ) {
                when (activeSheet) {
                    ActiveSheet.SKIN_TYPE -> SkinTypePicker(uiState, onEvent, ::hideBottomSheet)
                    ActiveSheet.CLOTHING -> ClothingPicker(uiState, onEvent, ::hideBottomSheet)
                    ActiveSheet.SUNSCREEN -> SunscreenPicker(uiState, onEvent, ::hideBottomSheet)
                    ActiveSheet.NONE -> {}
                }
            }
        }
    }
}

@Composable
fun Header() {
    Text(
        text = "SUN DAY",
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun UvSection(currentUv: Double) {
    Card(
        title = "UV INDEX",
        value = String.format("%.1f", currentUv)
    )
}

@Composable
fun VitaminDSection(vitaminDRate: Double) {
    Card(
        title = "VITAMIN D (IU/min)",
        value = String.format("%.0f", vitaminDRate / 60)
    )
}

@Composable
fun SettingsSection(
    uiState: UiState,
    onSkinTypeClick: () -> Unit,
    onClothingClick: () -> Unit,
    onSunscreenClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingButton(title = "CLOTHING", value = uiState.userPreferences.clothingLevel.shortDescription, onClick = onClothingClick, modifier = Modifier.weight(1f))
        SettingButton(title = "SUNSCREEN", value = uiState.userPreferences.sunscreen.displayName, onClick = onSunscreenClick, modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    SettingButton(title = "SKIN TYPE", value = uiState.userPreferences.skinType.fitzpatrickName, onClick = onSkinTypeClick)
}

@Composable
fun SettingButton(title: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        color = Color.Black.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun Card(title: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}