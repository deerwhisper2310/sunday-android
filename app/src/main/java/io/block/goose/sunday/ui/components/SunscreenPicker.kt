package io.block.goose.sunday.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.block.goose.sunday.domain.model.Sunscreen
import io.block.goose.sunday.ui.UiEvent
import io.block.goose.sunday.ui.UiState

@Composable
fun SunscreenPicker(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(Sunscreen.values()) { sunscreen ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(UiEvent.SunscreenChanged(sunscreen))
                        onDismiss()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = sunscreen.displayName, fontWeight = FontWeight.Bold)
                    Text(text = sunscreen.description, fontSize = 12.sp)

                }
                if (uiState.userPreferences.sunscreen == sunscreen) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                }
            }
        }
    }
}
