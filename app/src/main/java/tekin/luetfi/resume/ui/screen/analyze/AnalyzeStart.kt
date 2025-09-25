@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.getTextOrNull

@Composable
fun AnalyzeStart(
    modifier: Modifier = Modifier,
    isAnalyzing: Boolean = false,
    initialText: String = "",
    onAnalyze: (String, AnalyzeModel) -> Unit = { _, _ -> }
) {
    val focus = LocalFocusManager.current
    val clipboard = LocalClipboard.current
    val context = LocalContext.current

    var selectedModel by remember { mutableStateOf(AnalyzeModel.GROK_4_FAST) }


    var jobDescription by remember(initialText) { mutableStateOf(initialText) }

    val minChars = 50
    val maxChars = 10000
    val tooShort = jobDescription.trim().length in 1 until minChars
    val tooLong = jobDescription.length > maxChars
    val isValid = jobDescription.trim().length >= minChars && !tooLong

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Analyze a job description",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Quick actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                enabled = !isAnalyzing,
                onClick = {
                    clipboard.nativeClipboard.getTextOrNull(context)?.let { jobDescription = it }
                },
                label = { Text("Paste from clipboard") }
            )
            AssistChip(
                enabled = jobDescription.isNotBlank() && !isAnalyzing,
                onClick = { jobDescription = "" },
                label = { Text("Clear") }
            )
        }

        ModelPicker(
            selected = selectedModel,
            onSelect = { selectedModel = it })


        OutlinedTextField(
            value = jobDescription,
            onValueChange = { new ->
                jobDescription = if (new.length <= maxChars) new else new.take(maxChars)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            label = { Text("Job description") },
            placeholder = { Text("Paste the full posting: responsibilities, requirements, nice to have, languages, location, and company info.") },
            minLines = 6,
            maxLines = 12,
            enabled = !isAnalyzing,
            isError = tooShort || tooLong,
            supportingText = {
                val len = jobDescription.length
                val helper = when {
                    tooShort -> "Add more details for a reliable analysis: at least $minChars characters."
                    tooLong -> "You reached the limit of $maxChars characters. Consider trimming the posting."
                    else -> "Tip: include stack, seniority, location, language needs, and company context."
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(modifier = Modifier.align(Alignment.End), text = "$len/$maxChars")
                    Text(modifier = Modifier.fillMaxWidth(), text = helper)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isValid && !isAnalyzing) {
                        focus.clearFocus()
                        onAnalyze(jobDescription.trim(), AnalyzeModel.GROK_4_FAST)
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        AnimatedVisibility(visible = jobDescription.isBlank()) {
            Text(
                "Privacy note: nothing is stored locally after analysis unless you save it explicitly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                enabled = isValid && !isAnalyzing,
                onClick = { onAnalyze(jobDescription.trim(), selectedModel) }
            ) {
                Text(if (isAnalyzing) "Analyzing…" else "Analyze")
            }
        }
    }
}

@Composable
fun ModelPicker(
    selected: AnalyzeModel,
    onSelect: (AnalyzeModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Model") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AnalyzeModel.entries.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${model.displayName} • ${
                                model.category.name.lowercase().replaceFirstChar { it.uppercase() }
                            }"
                        )
                    },
                    onClick = {
                        onSelect(model)
                        expanded = false
                    }
                )
            }
        }
    }
}

