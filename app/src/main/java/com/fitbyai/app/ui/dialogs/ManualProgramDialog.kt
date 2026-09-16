package com.fitbyai.app.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fitbyai.app.data.ExerciseJson
import com.fitbyai.app.data.ProgramJsonPayload
import com.fitbyai.app.i18n.LocalAppStrings
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualProgramDialog(
    initialWeight: String = "",
    initialWaist: String = "",
    onDismiss: () -> Unit,
    onSaveProgram: (
        jsonRaw: String, weight: String, waist: String,
        sleep: String, energy: Int, rpe: Int, pain: String, feedback: String
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form inputs for current exercise draft
    var title by remember { mutableStateOf("") }
    var weeklySetsText by remember { mutableStateOf("4") }
    var targetPerSet by remember { mutableStateOf("10-12 reps (RIR 2)") }
    var targetMuscle by remember { mutableStateOf("Chest") }
    var description by remember { mutableStateOf("") }
    var dayNumber by remember { mutableStateOf(1) }

    val weight = initialWeight
    val waist = initialWaist

    val addedExercises = remember { mutableStateListOf<ExerciseJson>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val muscleOptions = listOf(
        strings.muscleChest,
        strings.muscleBack,
        strings.muscleLegs,
        strings.muscleShoulders,
        strings.muscleArms,
        strings.muscleAbs,
        strings.muscleOther
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.EditNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = strings.manualProgramTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.manualProgramDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Input Card for Adding Exercise
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Add Exercise",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Exercise Title") },
                        leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Day Allocation Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Workout Day",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (1..5).forEach { day ->
                                FilterChip(
                                    selected = dayNumber == day,
                                    onClick = { dayNumber = day },
                                    label = { Text(strings.dayN(day), style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Weekly Sets
                    OutlinedTextField(
                        value = weeklySetsText,
                        onValueChange = { weeklySetsText = it },
                        label = { Text("Sets") },
                        leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Target Per Set
                    OutlinedTextField(
                        value = targetPerSet,
                        onValueChange = { targetPerSet = it },
                        label = { Text(strings.targetLabel) },
                        leadingIcon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Target Muscle Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Target Muscle",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            muscleOptions.take(4).forEach { option ->
                                FilterChip(
                                    selected = targetMuscle == option,
                                    onClick = { targetMuscle = option },
                                    label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            muscleOptions.drop(4).forEach { option ->
                                FilterChip(
                                    selected = targetMuscle == option,
                                    onClick = { targetMuscle = option },
                                    label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Form Tips (Optional)") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                        singleLine = false,
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Add Exercise Button
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter an exercise title."
                                return@Button
                            }
                            val sets = weeklySetsText.toIntOrNull() ?: 4
                            if (sets <= 0) {
                                errorMessage = "Sets must be at least 1."
                                return@Button
                            }

                            val generatedId = title.trim().lowercase().replace(" ", "_")
                            addedExercises.add(
                                ExerciseJson(
                                    id = generatedId,
                                    title = title.trim(),
                                    description = description.ifEmpty { strings.defaultTarget },
                                    images = emptyList(),
                                    weeklySets = sets,
                                    targetPerSet = targetPerSet.ifEmpty { "10-12 reps" },
                                    movementPattern = "",
                                    targetMuscle = targetMuscle,
                                    day = dayNumber
                                )
                            )
                            title = ""
                            description = ""
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Exercise", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // List of Added Exercises
            if (addedExercises.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    addedExercises.forEachIndexed { index, ex ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}. ${ex.title}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${strings.dayN(ex.day ?: 1)} • ${ex.weeklySets} sets",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = ex.targetMuscle ?: "General",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { addedExercises.removeAt(index) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Final Save Button
            Button(
                onClick = {
                    if (addedExercises.isEmpty()) {
                        errorMessage = "Please add at least one exercise."
                        return@Button
                    }
                    val payload = ProgramJsonPayload(exercises = addedExercises.toList())
                    val jsonRaw = Gson().toJson(payload)
                    onSaveProgram(
                        jsonRaw, weight, waist,
                        "7.5", 8, 7, "No pain", "Manual Program"
                    )
                    onDismiss()
                },
                enabled = addedExercises.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${strings.saveManualProgramBtn} (${addedExercises.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
