@file:OptIn(ExperimentalMaterial3Api::class)

package tekin.luetfi.resume.ui.screen.analyze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.AnalyzeModel
import tekin.luetfi.resume.domain.model.MatchResponse
import tekin.luetfi.resume.util.getTextOrNull

@Composable
fun AnalyzeStart(
    modifier: Modifier = Modifier,
    isAnalyzing: Boolean = false,
    initialText: String = "",
    previousReports: List<MatchResponse>,
    viewModel: AnalyzeJobViewModel,
    onAnalyze: (String, List<AnalyzeModel>) -> Unit = { _, _ -> }
) {
    val focus = LocalFocusManager.current
    val clipboard = LocalClipboard.current
    val context = LocalContext.current

    val availableModelsViewModel: AvailableModelsViewModel = hiltViewModel()

    val availableModels by availableModelsViewModel.models.collectAsStateWithLifecycle(listOf(AnalyzeModel.default))

    var selectedModel by remember { mutableStateOf(availableModels.first()) }

    var selectedModels: List<AnalyzeModel> by remember(selectedModel) { mutableStateOf(listOf(selectedModel)) }

    var jobDescription by rememberSaveable(initialText) { mutableStateOf(initialText) }

    val minChars = 50
    val maxChars = 10000
    val tooShort = jobDescription.trim().length in 1 until minChars
    val tooLong = jobDescription.length > maxChars
    val isValid = jobDescription.trim().length >= minChars && !tooLong

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                stringResource(R.string.analyze_a_job_description),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Quick actions row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    enabled = !isAnalyzing,
                    onClick = {
                        clipboard.nativeClipboard.getTextOrNull(context)?.let { jobDescription = it }
                    },
                    label = { Text(stringResource(R.string.paste_from_clipboard)) }
                )
                AssistChip(
                    enabled = jobDescription.isNotBlank() && !isAnalyzing,
                    onClick = { jobDescription = "" },
                    label = { Text(stringResource(R.string.clear)) }
                )
            }
        }

        item {
            ModelPicker(
                selected = selectedModel,
                onSelect = {
                    selectedModel = it
                },
                availableModels = availableModels,
                onMultipleModelsSelected = {
                    selectedModels = it
                })
        }


        item {
            OutlinedTextField(
                value = jobDescription,
                onValueChange = { new ->
                    jobDescription = if (new.length <= maxChars) new else new.take(maxChars)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                label = { Text(stringResource(R.string.label_job_description)) },
                placeholder = { Text(stringResource(R.string.hint_job_description)) },
                minLines = 6,
                maxLines = 12,
                enabled = !isAnalyzing,
                isError = tooShort || tooLong,
                supportingText = {
                    val len = jobDescription.length
                    val helper = when {
                        tooShort -> stringResource(
                            R.string.error_job_description_too_short,
                            minChars
                        )
                        tooLong -> stringResource(R.string.error_job_description_too_long, maxChars)
                        else -> stringResource(R.string.hint_job_description_tip)
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
                            onAnalyze(jobDescription.trim(), selectedModels)
                        }
                    }
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        item {
            AnimatedVisibility(visible = jobDescription.isBlank()) {
                Text(
                    stringResource(R.string.privacy_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    enabled = isValid && !isAnalyzing && selectedModels.isNotEmpty(),
                    onClick = { onAnalyze(jobDescription.trim(), selectedModels) }
                ) {
                    Text(if (isAnalyzing) stringResource(R.string.analyzing) else stringResource(R.string.analyze))
                }
            }
        }

        item {
            PreviousReports(
                modifier = Modifier.fillMaxWidth(),
                previousReports = previousReports,
                viewModel = viewModel)
        }
    }
}

@Composable
fun ModelPicker(
    selected: AnalyzeModel,
    onSelect: (AnalyzeModel) -> Unit,
    availableModels: List<AnalyzeModel>,
    onMultipleModelsSelected: (List<AnalyzeModel>) -> Unit = {}
) {
    var selectMultipleModels by remember { mutableStateOf(false) }
    var selectedModels by remember { mutableStateOf(setOf<AnalyzeModel>()) }
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Single model dropdown (shown when multiple models is unchecked)
        if (!selectMultipleModels) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selected.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.model)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    availableModels.forEach { model ->
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

        Spacer(modifier = Modifier.height(16.dp))

        // Multiple models checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selectMultipleModels,
                onCheckedChange = { checked ->
                    selectMultipleModels = checked
                    if (!checked) {
                        // Reset to single model when unchecked
                        selectedModels = emptySet()
                        onMultipleModelsSelected(emptyList())
                    } else {
                        // Initialize with currently selected model
                        selectedModels = setOf(selected)
                        onMultipleModelsSelected(listOf(selected))
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Use multiple models",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Multiple models selection (shown when checkbox is checked)
        if (selectMultipleModels) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Models:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Model selection using FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableModels.forEach { model ->
                    ModelSelectionChip(
                        model = model,
                        isSelected = selectedModels.contains(model),
                        onSelectionChange = { isSelected ->
                            val newSelection = if (isSelected) {
                                selectedModels + model
                            } else {
                                selectedModels - model
                            }
                            selectedModels = newSelection
                            onMultipleModelsSelected(newSelection.toList())
                        }
                    )
                }
            }

            // Selected models summary
            if (selectedModels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Selected: ${selectedModels.size} model(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModelSelectionChip(
    model: AnalyzeModel,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    FilterChip(
        modifier = Modifier.animateContentSize(),
        onClick = { onSelectionChange(!isSelected) },
        label = {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = model.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = model.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}


