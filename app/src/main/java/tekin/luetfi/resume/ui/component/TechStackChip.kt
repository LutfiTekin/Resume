package tekin.luetfi.resume.ui.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TechStackChip(tech: String) {
    AssistChip(
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            labelColor = MaterialTheme.colorScheme.onSecondary
        ),
        onClick = {},
        label = {
            Text(
                text = tech
            )
        })
}