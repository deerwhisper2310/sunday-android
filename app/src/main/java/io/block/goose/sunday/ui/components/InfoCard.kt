package io.block.goose.sunday.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VitaminDInfoCard(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Vitamin D Calculation")
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("The app estimates the rate of Vitamin D (in IU/min) your body can produce based on several key factors:")
                Spacer(modifier = Modifier.height(16.dp))

                Text("1. UV Index: Higher UV levels increase production, but the effect plateaus at very high indexes.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("2. Skin Type: Lighter skin tones synthesize Vitamin D more efficiently than darker tones.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("3. Clothing: The more skin you have covered, the lower the Vitamin D production.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("4. Sunscreen: Sunscreen blocks the UVB rays necessary for Vitamin D synthesis, significantly reducing production.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("5. Age: The body's ability to produce Vitamin D naturally declines with age.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("6. Time of Day: Production is most efficient around solar noon when the sun is at its highest point.")
                Spacer(modifier = Modifier.height(8.dp))

                Text("7. Recent Sun Exposure: Your body adapts to regular sun exposure, which can influence your production rate.")
                Spacer(modifier = Modifier.height(16.dp))

                Text("This calculation is an estimate and should not be considered medical advice.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
