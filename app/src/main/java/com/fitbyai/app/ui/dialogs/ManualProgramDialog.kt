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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fitbyai.app.data.ExerciseJson
import com.fitbyai.app.data.ProgramJsonPayload
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Form inputs for current exercise draft
    var title by remember { mutableStateOf("") }
    var weeklySetsText by remember { mutableStateOf("6") }
    var targetPerSet by remember { mutableStateOf("۱۰ الی ۱۲ تکرار (RIR 2)") }
    var targetMuscle by remember { mutableStateOf("سینه") }
    var description by remember { mutableStateOf("") }

    // Recovery inputs for archiving current week if applicable
    val weight = initialWeight
    val waist = initialWaist

    // List of added exercises
    val addedExercises = remember { mutableStateListOf<ExerciseJson>() }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val muscleOptions = listOf("سینه", "پشت", "پا", "شانه", "بازو", "شکم", "کل بدن")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                            text = "طراحی دستی برنامه تمرینی",
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
                        text = "در این بخش می‌توانید بدون نیاز به هوش مصنوعی، حرکات تمرینی دلخواه خود را همراه با تمام فیلدها (تعداد ست، عضله هدف، تکرار و...) دستی وارد کرده و برنامه خود را بسازید.",
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
                            text = "افزودن حرکت جدید به برنامه",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("نام حرکت (مثال: پرس سینه با دمبل)") },
                            leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Weekly Sets (Select All on focus/click)
                        SelectAllOutlinedTextField(
                            value = weeklySetsText,
                            onValueChange = { weeklySetsText = it },
                            label = { Text("ست هفتگی") },
                            leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Target Per Set (Takes full width)
                        OutlinedTextField(
                            value = targetPerSet,
                            onValueChange = { targetPerSet = it },
                            label = { Text("هدف/تکرار هر ست") },
                            leadingIcon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Target Muscle Selection Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "عضله هدف",
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

                        // Description (Multi-line with 3 lines height for easy text entry)
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("توضیحات و نکات تکنیکی (اختیاری)") },
                            leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                            singleLine = false,
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Add Exercise Button
                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    errorMessage = "لطفاً نام حرکت را وارد کنید."
                                    return@Button
                                }
                                val sets = weeklySetsText.toIntOrNull() ?: 6
                                if (sets <= 0) {
                                    errorMessage = "تعداد ست‌های هفتگی باید حداقل ۱ باشد."
                                    return@Button
                                }

                                val generatedId = title.trim().lowercase().replace(" ", "_")
                                addedExercises.add(
                                    ExerciseJson(
                                        id = generatedId,
                                        title = title.trim(),
                                        description = description.ifEmpty { "اجرا بر اساس تکنیک صحیح" },
                                        images = emptyList(),
                                        weeklySets = sets,
                                        targetPerSet = targetPerSet.ifEmpty { "۱۰ الی ۱۲ تکرار" },
                                        movementPattern = "",
                                        targetMuscle = targetMuscle
                                    )
                                )
                                // Reset title and description for next input
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
                            Text("افزودن این حرکت به لیست", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // List of Added Exercises
                Text(
                    text = "حرکات افزوده شده به برنامه دستی (${addedExercises.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (addedExercises.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز هیچ حرکتی اضافه نکرده‌اید. مشخصات بالا را پر کرده و روی «افزودن این حرکت» بزنید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
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
                                                    text = "${ex.weeklySets} ست هفتگی",
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
                                                    text = ex.targetMuscle ?: "عمومی",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "هدف هر ست: ${ex.targetPerSet ?: "-"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { addedExercises.removeAt(index) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف حرکت",
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
                            errorMessage = "لطفاً حداقل یک حرکت به لیست اضافه کنید."
                            return@Button
                        }
                        val payload = ProgramJsonPayload(exercises = addedExercises.toList())
                        val jsonRaw = Gson().toJson(payload)
                        onSaveProgram(
                            jsonRaw, weight, waist,
                            "7.5", 8, 7, "بدون درد", "برنامه دستی کاربر"
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
                        text = "ثبت و فعال‌سازی برنامه دستی (${addedExercises.size} حرکت)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
