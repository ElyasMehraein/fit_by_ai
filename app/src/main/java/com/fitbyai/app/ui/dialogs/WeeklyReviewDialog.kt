package com.fitbyai.app.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReviewDialog(
    generatedPrompt: String?,
    errorMessage: String?,
    isLoading: Boolean,
    initialWeight: String = "",
    initialWaist: String = "",
    onDismiss: () -> Unit,
    onEditProfile: () -> Unit,
    onGeneratePrompt: (weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String) -> Unit,
    onImportProgram: (jsonRaw: String, weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String) -> Unit
) {
    val context = LocalContext.current

    var weight by remember { mutableStateOf(initialWeight) }
    var waist by remember { mutableStateOf(initialWaist) }
    var energy by remember { mutableStateOf(8f) }
    var rpe by remember { mutableStateOf(7f) }
    var pain by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var jsonInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Action Bar (Minimal)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                    TextButton(onClick = onEditProfile) {
                        Text("ویرایش پروفایل اصلی", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // Compact Metrics Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricInput(value = weight, onValueChange = { weight = it }, label = "وزن (kg)", modifier = Modifier.weight(1f))
                    MetricInput(value = waist, onValueChange = { waist = it }, label = "کمر (cm)", modifier = Modifier.weight(1f))
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("انرژی و سختی تمرین (۱-۱۰)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Slider(value = energy, onValueChange = { energy = it }, valueRange = 1f..10f, steps = 8, modifier = Modifier.weight(1f))
                        Text("${energy.toInt()}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Slider(value = rpe, onValueChange = { rpe = it }, valueRange = 1f..10f, steps = 8, modifier = Modifier.weight(1f))
                        Text("${rpe.toInt()}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // AI Action
                Button(
                    onClick = { onGeneratePrompt(weight, waist, "7.5", energy.toInt(), rpe.toInt(), pain, feedback) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تولید متن برای هوش مصنوعی", style = MaterialTheme.typography.titleSmall)
                }

                if (!generatedPrompt.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("AI Prompt", generatedPrompt))
                                Toast.makeText(context, "کپی شد!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(12.dp)
                    ) {
                        Text(
                            "متن آماده شد. برای کپی کلیک کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

                // Import Action
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        placeholder = { Text("کد برنامه را اینجا بچسبانید (JSON)", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = { onImportProgram(jsonInput, weight, waist, "7.5", energy.toInt(), rpe.toInt(), pain, feedback) },
                        enabled = !isLoading && jsonInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        else Text("شروع هفته جدید", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricInput(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}
