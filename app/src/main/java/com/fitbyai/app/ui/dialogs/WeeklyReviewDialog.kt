package com.fitbyai.app.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitbyai.app.i18n.AppLanguage
import com.fitbyai.app.i18n.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReviewDialog(
    generatedPrompt: String?,
    errorMessage: String?,
    isLoading: Boolean,
    initialWeight: String = "",
    initialWaist: String = "",
    profileLastUpdatedText: String = "Just now",
    currentLanguage: AppLanguage = AppLanguage.DEFAULT,
    onDismiss: () -> Unit,
    onEditProfile: () -> Unit,
    onGeneratePrompt: (weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String, muscleSoreness: String, jointPain: String) -> Unit,
    onImportProgram: (jsonRaw: String, weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String, muscleSoreness: String, jointPain: String) -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val composeClipboardManager = LocalClipboardManager.current

    val weight = initialWeight
    val waist = initialWaist
    var sleepHours by remember { mutableStateOf(7.5f) }
    var energy by remember { mutableStateOf(8f) }
    var rpe by remember { mutableStateOf(7f) }

    val isFa = currentLanguage == AppLanguage.PERSIAN

    val noJointPainText = if (isFa) "بدون درد مفصلی" else strings.jointPainNone
    val defaultSorenessText = if (isFa) "عادی / خفیف" else strings.sorenessNormal

    var muscleSoreness by remember { mutableStateOf(defaultSorenessText) }
    var jointPainSet by remember { mutableStateOf(setOf(noJointPainText)) }
    var feedback by remember { mutableStateOf("") }
    var jsonInput by remember { mutableStateOf("") }

    val jointOptions = if (isFa) {
        listOf("بدون درد مفصلی", "شانه", "زانو", "مچ دست / آرنج", "کمر / ستون فقرات", "مچ پا")
    } else {
        listOf(strings.jointPainNone, "Shoulders", "Knees", "Wrists / Elbows", "Lower Back", "Ankles")
    }

    val sorenessOptions = if (isFa) {
        listOf("عادی / خفیف", "متوسط", "شدید (>۴۸ساعت)")
    } else {
        listOf(strings.sorenessNormal, strings.sorenessHigh, strings.sorenessExtreme)
    }

    val finalJointPain = remember(jointPainSet, noJointPainText) {
        if (jointPainSet.contains(noJointPainText) || jointPainSet.isEmpty()) noJointPainText else jointPainSet.joinToString("، ")
    }

    var detectedClipboardJson by remember { mutableStateOf<String?>(null) }

    // Auto-detect JSON in clipboard when dialog opens
    LaunchedEffect(Unit) {
        val clipText = composeClipboardManager.getText()?.text
        if (!clipText.isNullOrBlank() && (clipText.contains("exerciseId") || clipText.contains("taskId") || clipText.trim().startsWith("["))) {
            detectedClipboardJson = clipText
            if (jsonInput.isBlank()) {
                jsonInput = clipText
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strings.weeklyReviewTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = strings.profileLastUpdated(profileLastUpdatedText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "• ${strings.editProfileBtn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onEditProfile() }
                    )
                }
            }

            // Compact Metrics Grid
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(strings.reviewIntroNotice, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Sleep Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.sleepLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format("%.1f", sleepHours)} ${strings.minutesSuffix.replace("min", "h")}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sleepHours,
                            onValueChange = { sleepHours = kotlin.math.round(it * 2f) / 2f },
                            valueRange = 4f..12f,
                            steps = 15,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Energy Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.energyLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${energy.toInt()} / 10", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = energy,
                            onValueChange = { energy = kotlin.math.round(it) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // RPE Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.rpeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${rpe.toInt()} / 10", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = rpe,
                            onValueChange = { rpe = kotlin.math.round(it) },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Soreness Section
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(strings.muscleSorenessLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                sorenessOptions.forEach { option ->
                                    val isSelected = muscleSoreness == option
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { muscleSoreness = option },
                                        label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }

                        // Joint Pain Section
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(strings.jointPainLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            jointOptions.chunked(3).forEach { rowOptions ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowOptions.forEach { option ->
                                        val isSelected = jointPainSet.contains(option)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                if (option == noJointPainText) {
                                                    jointPainSet = setOf(noJointPainText)
                                                } else {
                                                    val nextSet = jointPainSet.toMutableSet()
                                                    nextSet.remove(noJointPainText)
                                                    if (nextSet.contains(option)) {
                                                        nextSet.remove(option)
                                                    } else {
                                                        nextSet.add(option)
                                                    }
                                                    jointPainSet = if (nextSet.isEmpty()) setOf(noJointPainText) else nextSet
                                                }
                                            },
                                            label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // AI Prompt Generation Action
            Button(
                onClick = {
                    onGeneratePrompt(
                        weight, waist, String.format("%.1f", sleepHours),
                        energy.toInt(), rpe.toInt(), finalJointPain, feedback,
                        muscleSoreness, finalJointPain
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.generatePromptBtn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (!generatedPrompt.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.generatedPromptTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strings.promptInstructions,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Copy Prompt Button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("AI Prompt", generatedPrompt))
                                    Toast.makeText(context, strings.promptCopiedToast, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = strings.copyPromptBtn,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.copyPromptBtn,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Send to ChatGPT Button
                            Button(
                                onClick = {
                                    openChatGPT(context, generatedPrompt, strings.promptCopiedToast)
                                },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10A37F),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "ChatGPT",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ChatGPT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // Import JSON Program Section
            var jsonTextFieldState by remember { mutableStateOf(TextFieldValue(text = jsonInput)) }
            var isJsonFocused by remember { mutableStateOf(false) }

            LaunchedEffect(jsonInput) {
                if (jsonInput != jsonTextFieldState.text) {
                    jsonTextFieldState = TextFieldValue(text = jsonInput, selection = TextRange(jsonInput.length))
                }
            }

            LaunchedEffect(isJsonFocused) {
                if (isJsonFocused && jsonTextFieldState.text.isNotEmpty()) {
                    kotlinx.coroutines.delay(50)
                    jsonTextFieldState = jsonTextFieldState.copy(selection = TextRange(0, jsonTextFieldState.text.length))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.importProgramTitle, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)

                    FilledTonalButton(
                        onClick = {
                            val clipText = composeClipboardManager.getText()?.text
                            if (!clipText.isNullOrBlank()) {
                                jsonInput = clipText
                                jsonTextFieldState = TextFieldValue(text = clipText, selection = TextRange(clipText.length))
                                Toast.makeText(context, "Pasted ✅", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paste", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = jsonTextFieldState,
                    onValueChange = {
                        jsonTextFieldState = it
                        jsonInput = it.text
                    },
                    label = { Text(strings.jsonPlaceholder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .onFocusChanged { isJsonFocused = it.isFocused },
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        textDirection = TextDirection.Ltr
                    )
                )

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        onImportProgram(
                            jsonInput, weight, waist,
                            String.format("%.1f", sleepHours), energy.toInt(), rpe.toInt(),
                            finalJointPain, feedback, muscleSoreness, finalJointPain
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = jsonInput.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.importProgramBtn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun openChatGPT(context: Context, promptText: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("AI Prompt", promptText))

    val chatGptPackage = "com.openai.chatgpt"

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, promptText)
        setPackage(chatGptPackage)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val isAppInstalled = try {
        context.packageManager.getPackageInfo(chatGptPackage, 0)
        true
    } catch (e: Exception) {
        false
    }

    if (isAppInstalled) {
        try {
            context.startActivity(sendIntent)
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            return
        } catch (e: Exception) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(chatGptPackage)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    try {
        val encodedPrompt = Uri.encode(promptText)
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chatgpt.com/?q=$encodedPrompt")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(webIntent)
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
    }
}
