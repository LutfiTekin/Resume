package tekin.luetfi.resume.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.util.computePeriod
import tekin.luetfi.resume.domain.model.ExperienceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceCard(
    item: ExperienceItem,
    onClick: () -> Unit
) {
    val period = computePeriod(item.period)
    val techStackClickHandler = rememberTechStackClickHandler()

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = item.company,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = "${period.startLabel} - ${period.endLabel} (${period.durationText})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${item.location} | ${item.type}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.notes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item.notes.forEach { note ->
                        Text("• $note", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (item.stack.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.stack.forEach { tech ->
                        TechStackChip(tech){
                            techStackClickHandler(tech)
                        }
                    }
                }
            }
        }
    }
}

