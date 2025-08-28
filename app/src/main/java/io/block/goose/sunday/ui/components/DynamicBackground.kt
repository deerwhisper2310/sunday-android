package io.block.goose.sunday.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.block.goose.sunday.ui.theme.getDynamicGradientBrush

@Composable
fun DynamicBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.background(brush = getDynamicGradientBrush())
    ) {
        content()
    }
}
