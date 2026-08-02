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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "پروفایل ورزشی شما",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "این اطلاعات برای تنظیم برنامه‌های تمرینی هوش مصنوعی بر اساس آناتومی شما استفاده می‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("قد (cm)") },
                    leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("سن") },
                    leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = baseWeight,
                    onValueChange = { baseWeight = it },
                    label = { Text("وزن (kg)") },
                    leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = baseWaist,
                    onValueChange = { baseWaist = it },
                    label = { Text("دور کمر (cm)") },
                    leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("هدف اصلی ورزشی") },
                leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text("سابقه تمرین") },
                leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = daysPerWeek,
                onValueChange = { daysPerWeek = it },
                label = { Text("روزهای تمرین در هفته") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = equipment,
                onValueChange = { equipment = it },
                label = { Text("تجهیزات در دسترس") },
                leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = limitations,
                onValueChange = { limitations = it },
                label = { Text("محدودیت‌ها و آسیب‌های قبلی") },
                leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
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
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("ذخیره پروفایل ورزشی", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
