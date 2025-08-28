package io.block.goose.sunday.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.block.goose.sunday.domain.model.SkinType
import io.block.goose.sunday.ui.UiEvent
import io.block.goose.sunday.ui.UiState

@Composable
fun SkinTypePicker(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(SkinType.values()) { skinType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(UiEvent.SkinTypeChanged(skinType))
                        onDismiss()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(skinType.color)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = skinType.displayName, fontWeight = FontWeight.Bold)
                    Text(text = skinType.description, fontSize = 12.sp)
                }
                if (uiState.userPreferences.skinType == skinType) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                }
            }
        }
    }
}
