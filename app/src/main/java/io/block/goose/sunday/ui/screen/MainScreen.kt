package io.block.goose.sunday.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import io.block.goose.sunday.ui.components.AgePicker
import io.block.goose.sunday.ui.components.ClothingPicker
import io.block.goose.sunday.ui.components.DynamicBackground
import io.block.goose.sunday.ui.components.SkinTypePicker
import io.block.goose.sunday.ui.components.SunscreenPicker
import io.block.goose.sunday.ui.components.VitaminDInfoCard
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private enum class ActiveSheet {
    NONE, SKIN_TYPE, CLOTHING, SUNSCREEN, AGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier

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

    DynamicBackground(
        modifier = Modifier.fillMaxSize() // DynamicBackground itself should fill the physical screen
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding() // Apply safeDrawingPadding to this wrapping Box
        ) {
            when (val uvDataState = uiState.uvDataState) {
                is UvDataState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // No safeDrawingPadding here
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
                            .fillMaxSize() // No safeDrawingPadding here
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Header(onInfoClick = { onEvent(UiEvent.InfoClicked) })
                        Spacer(modifier = Modifier.height(20.dp))
                        UvSection(
                            currentUv = currentUv,
                            burnTime = uiState.burnTime,
                            maxUv = uiState.maxUv,
                            sunrise = uiState.sunrise,
                            sunset = uiState.sunset
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        VitaminDSection(vitaminDRate = uiState.vitaminDRate)
                        Spacer(modifier = Modifier.height(32.dp))
                        SettingsSection(
                            uiState = uiState,
                            onSkinTypeClick = { activeSheet = ActiveSheet.SKIN_TYPE },
                            onClothingClick = { activeSheet = ActiveSheet.CLOTHING },
                            onSunscreenClick = { activeSheet = ActiveSheet.SUNSCREEN },
                            onAgeClick = { activeSheet = ActiveSheet.AGE }
                        )
                    }
                }
                is UvDataState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // No safeDrawingPadding here
                        Text(
                            text = "Error: ${uvDataState.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        if (uiState.isInfoCardVisible) {
            VitaminDInfoCard(uiState = uiState, onDismiss = { onEvent(UiEvent.InfoCardDismissed) })
        }

        if (activeSheet != ActiveSheet.NONE) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = ActiveSheet.NONE },
                sheetState = sheetState,
                windowInsets = WindowInsets(0, 0, 0, 0) // ADDED: To make sheet extend full screen
            ) {
                Column(modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())) { // ADDED: Apply padding to content
                    when (activeSheet) {
                        ActiveSheet.SKIN_TYPE -> SkinTypePicker(uiState, onEvent, ::hideBottomSheet)
                        ActiveSheet.CLOTHING -> ClothingPicker(uiState, onEvent, ::hideBottomSheet)
                        ActiveSheet.SUNSCREEN -> SunscreenPicker(uiState, onEvent, ::hideBottomSheet)
                        ActiveSheet.AGE -> AgePicker(uiState, onEvent, ::hideBottomSheet)
                        ActiveSheet.NONE -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun Header(onInfoClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "SUN DAY",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Information",
                tint = Color.White
            )
        }
    }
}

@Composable
fun UvSection(
    currentUv: Double,
    burnTime: Int?,
    maxUv: Double?,
    sunrise: String?,
    sunset: String?
) {
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
                text = "UV INDEX",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = String.format("%.1f", currentUv),
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "BURN LIMIT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = burnTime?.let {
                            if (it >= 1440) {
                                "---"
                            } else if (it >= 60) {
                                val hours = it / 60
                                val minutes = it % 60
                                "${hours}h ${minutes}m"
                            } else {
                                "${it}m"
                            }
                        } ?: "---",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MAX UVI",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (maxUv != null) String.format("%.1f", maxUv) else "---",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SUNRISE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = sunrise?.let { formatTime(it) } ?: "---",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SUNSET",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = sunset?.let { formatTime(it) } ?: "---",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun formatTime(dateTime: String): String {
    return try {
        val ldt = LocalDateTime.parse(dateTime)
        val formatter = DateTimeFormatter.ofPattern("h:mma")
        ldt.format(formatter)
    } catch (e: Exception) {
        "---"
    }
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
    onSunscreenClick: () -> Unit,
    onAgeClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingButton(title = "CLOTHING", value = uiState.userPreferences.clothingLevel.shortDescription, onClick = onClothingClick, modifier = Modifier.weight(1f))
        SettingButton(title = "SUNSCREEN", value = uiState.userPreferences.sunscreen.displayName, onClick = onSunscreenClick, modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingButton(title = "SKIN TYPE", value = uiState.userPreferences.skinType.fitzpatrickName, onClick = onSkinTypeClick, modifier = Modifier.weight(1f))
        SettingButton(title = "AGE", value = uiState.userPreferences.age.toString(), onClick = onAgeClick, modifier = Modifier.weight(1f))
    }
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