package io.block.goose.sunday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.block.goose.sunday.ui.UiState
import io.block.goose.sunday.ui.UiEvent

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon

@Composable
fun AgePicker(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items((0..100).toList()) { age ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(UiEvent.AgeChanged(age))
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = age.toString(), style = MaterialTheme.typography.bodyLarge)
                if (uiState.userPreferences.age == age) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected")
                }
            }
        }
    }
}
