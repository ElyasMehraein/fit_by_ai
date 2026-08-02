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
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReviewDialog(
    generatedPrompt: String?,
    errorMessage: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onGeneratePrompt: (weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String) -> Unit,
    onImportProgram: (jsonRaw: String, weight: String, waist: String, sleep: String, energy: Int, rpe: Int, pain: String, feedback: String) -> Unit
) {
    val context = LocalContext.current

    var weight by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var sleep by remember { mutableStateOf("7.5 ساعت") }
    var energy by remember { mutableStateOf(8) }
    var rpe by remember { mutableStateOf(7) }
    var pain by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var jsonInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "بروزرسانی هفتگی و دریافت برنامه",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = "اطلاعات ریکاوری و سنجش هفته گذشته را برای تولید پرامپت هوش مصنوعی وارد کنید.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("وزن جدید (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = waist,
                        onValueChange = { waist = it },
                        label = { Text("دور کمر جدید (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = energy.toString(),
                        onValueChange = { energy = it.toIntOrNull() ?: 8 },
                        label = { Text("انرژی (1-10)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = rpe.toString(),
                        onValueChange = { rpe = it.toIntOrNull() ?: 7 },
                        label = { Text("سختی RPE (1-10)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = pain,
                    onValueChange = { pain = it },
                    label = { Text("درد یا آسیب جدید (اختیاری)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("بازخورد و سابقه تمرین اخیر") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Button(
                    onClick = {
                        onGeneratePrompt(weight, waist, sleep, energy, rpe, pain, feedback)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🤖 تولید پرامپت هوش مصنوعی (درخواست عکس + تاریخچه)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }

                if (!generatedPrompt.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "متن پرامپت جهت ارسال به ChatGPT:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedTextField(
                                value = generatedPrompt ?: "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            )
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AI Prompt", generatedPrompt)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "پرامپت کپی شد!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("📋 کپی متن پرامپت", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline)

                Text(
                    text = "ورود ساختار برنامه تمرین (JSON)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
                    placeholder = { Text("{ \"exercises\": [ ... ] }") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                )

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        onImportProgram(jsonInput, weight, waist, sleep, energy, rpe, pain, feedback)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("▶ ایجاد صف تمرینات و شروع هفته جدید", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
