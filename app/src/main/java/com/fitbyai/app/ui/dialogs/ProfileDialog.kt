package com.fitbyai.app.ui.dialogs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.fitbyai.app.data.UserProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    currentProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (
        height: String, age: String, gender: String, goal: String,
        baseWeight: String, baseWaist: String, experience: String,
        daysPerWeek: String, equipment: String, limitations: String
    ) -> Unit
) {
    var height by remember { mutableStateOf(currentProfile?.height ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age ?: "") }
    var gender by remember { mutableStateOf(currentProfile?.gender ?: "مرد") }
    var goal by remember { mutableStateOf(currentProfile?.goal ?: "عضله‌سازی (حجم)") }
    var baseWeight by remember { mutableStateOf(currentProfile?.baseWeight ?: "") }
    var baseWaist by remember { mutableStateOf(currentProfile?.baseWaist ?: "") }
    var experience by remember { mutableStateOf(currentProfile?.experience ?: "متوسط (6 ماه تا 2 سال)") }
    var daysPerWeek by remember { mutableStateOf(currentProfile?.daysPerWeek ?: "4 روز در هفته") }
    var equipment by remember { mutableStateOf(currentProfile?.equipment ?: "") }
    var limitations by remember { mutableStateOf(currentProfile?.limitations ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پروفایل ورزشی",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Text(
                    text = "این اطلاعات جهت شخصی‌سازی برنامه‌های هوش مصنوعی استفاده می‌شوند.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("قد (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("سن") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = baseWeight,
                        onValueChange = { baseWeight = it },
                        label = { Text("وزن (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = baseWaist,
                        onValueChange = { baseWaist = it },
                        label = { Text("دور کمر (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("هدف اصلی ورزشی") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("سابقه تمرین") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = daysPerWeek,
                    onValueChange = { daysPerWeek = it },
                    label = { Text("روزهای تمرین در هفته") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = equipment,
                    onValueChange = { equipment = it },
                    label = { Text("تجهیزات در دسترس") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = limitations,
                    onValueChange = { limitations = it },
                    label = { Text("محدودیت‌ها و آسیب‌های قبلی") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        onSave(
                            height, age, gender, goal, baseWeight, baseWaist,
                            experience, daysPerWeek, equipment, limitations
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("ذخیره اطلاعات پروفایل", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
