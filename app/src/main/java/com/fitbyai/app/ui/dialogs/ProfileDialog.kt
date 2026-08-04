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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import com.fitbyai.app.data.UserProfileEntity
import com.fitbyai.app.data.WeeklyHistoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    currentProfile: UserProfileEntity?,
    weeklyHistory: List<WeeklyHistoryEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        height: String, age: String, gender: String, goal: String,
        baseWeight: String, baseWaist: String, experience: String,
        daysPerWeek: String, equipment: String, limitations: String,
        targetWeight: String, sessionDuration: String, activityLevel: String, healthConditions: String
    ) -> Unit,
    onResetData: () -> Unit = {}
) {
    var height by remember { mutableStateOf(currentProfile?.height ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age ?: "") }
    var gender by remember { mutableStateOf(currentProfile?.gender?.ifEmpty { "مرد" } ?: "مرد") }
    var goal by remember { mutableStateOf(currentProfile?.goal ?: "عضله‌سازی (حجم)") }
    var baseWeight by remember { mutableStateOf(currentProfile?.baseWeight ?: "") }
    var baseWaist by remember { mutableStateOf(currentProfile?.baseWaist ?: "") }
    var experience by remember {
        mutableStateOf(
            currentProfile?.experience?.filter { it.isDigit() }?.ifEmpty { "2" } ?: "2"
        )
    }
    var daysPerWeek by remember {
        mutableStateOf(
            currentProfile?.daysPerWeek?.filter { it.isDigit() }?.ifEmpty { "4" } ?: "4"
        )
    }
    var equipment by remember { mutableStateOf(currentProfile?.equipment ?: "") }
    var limitations by remember { mutableStateOf(currentProfile?.limitations ?: "") }
    var targetWeight by remember { mutableStateOf(currentProfile?.targetWeight ?: "") }
    var sessionDuration by remember {
        mutableStateOf(
            currentProfile?.sessionDuration?.filter { it.isDigit() }?.ifEmpty { "60" } ?: "60"
        )
    }
    var activityLevel by remember { mutableStateOf(currentProfile?.activityLevel ?: "کم‌تحرک (کارمندی)") }
    var healthConditions by remember { mutableStateOf(currentProfile?.healthConditions ?: "") }

    var selectedTab by remember { mutableStateOf(0) } // 0: Specs Form, 1: Progress History, 2: Settings & Reset

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        indicator = {},
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "مشخصات و فرم",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = { Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    "پیشرفت تمرینی",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Text(
                                    "تنظیمات و مدیریت",
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                if (selectedTab == 0) {
                    // TAB 0: Profile Specifications Form
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "این اطلاعات به طور کامل در پرامپت هوش مصنوعی قرار گرفته تا برنامه دقیقاً بر اساس فیزیک، اهداف و شرایط زندگی شما طراحی شود.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // Gender Selection Section
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "جنسیت",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = gender == "مرد",
                                onClick = { gender = "مرد" },
                                label = { Text("مرد 👨", modifier = Modifier.padding(vertical = 4.dp)) },
                                leadingIcon = if (gender == "مرد") {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            FilterChip(
                                selected = gender == "زن",
                                onClick = { gender = "زن" },
                                label = { Text("زن 👩", modifier = Modifier.padding(vertical = 4.dp)) },
                                leadingIcon = if (gender == "زن") {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else null,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SelectAllOutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("قد (cm)") },
                            leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        SelectAllOutlinedTextField(
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
                        SelectAllOutlinedTextField(
                            value = baseWeight,
                            onValueChange = { baseWeight = it },
                            label = { Text("وزن فعلی (kg)") },
                            leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        SelectAllOutlinedTextField(
                            value = targetWeight,
                            onValueChange = { targetWeight = it },
                            label = { Text("وزن هدف (kg)") },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SelectAllOutlinedTextField(
                            value = baseWaist,
                            onValueChange = { baseWaist = it },
                            label = { Text("دور شکم (cm)") },
                            leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        SelectAllOutlinedTextField(
                            value = daysPerWeek,
                            onValueChange = { daysPerWeek = it },
                            label = { Text("روزهای تمرین در هفته") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    SelectAllOutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        label = { Text("هدف اصلی ورزشی") },
                        leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SelectAllOutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text("سابقه تمرین (سال)") },
                            leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        SelectAllOutlinedTextField(
                            value = sessionDuration,
                            onValueChange = { sessionDuration = it },
                            label = { Text("زمان هر جلسه (دقیقه)") },
                            leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "سطح فعالیت روزمره",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val options = listOf(
                                "کم‌تحرک" to "کم‌تحرک (پشت‌میز نشینی)",
                                "نیمه‌فعال" to "نیمه‌فعال (تحرک متوسط)",
                                "پرتحرک" to "پرتحرک (فعالیت سنگین)"
                            )
                            options.forEach { (shortLabel, fullText) ->
                                val isSelected = activityLevel == fullText || activityLevel.startsWith(shortLabel)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { activityLevel = fullText },
                                    label = { Text(shortLabel, style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    SelectAllOutlinedTextField(
                        value = equipment,
                        onValueChange = { equipment = it },
                        label = { Text("تجهیزات در دسترس") },
                        leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    SelectAllOutlinedTextField(
                        value = limitations,
                        onValueChange = { limitations = it },
                        label = { Text("محدودیت‌ها و آسیب‌های قبلی") },
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    SelectAllOutlinedTextField(
                        value = healthConditions,
                        onValueChange = { healthConditions = it },
                        label = { Text("بیماری خاص یا داروهای مصرفی (در صورت وجود)") },
                        leadingIcon = { Icon(Icons.Default.Healing, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onSave(
                                height, age, gender, goal, baseWeight, baseWaist,
                                experience, daysPerWeek, equipment, limitations,
                                targetWeight, sessionDuration, activityLevel, healthConditions
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
                        Text(
                            "ذخیره پروفایل ورزشی",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (selectedTab == 1) {
                    // TAB 1: Workout Progress History (Moved from HistoryDialog)
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("کل هفته‌ها", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${weeklyHistory.size} هفته", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("آخرین ثبت وزن", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val lastW = if (weeklyHistory.isNotEmpty()) "${weeklyHistory.last().weight} kg" else "-"
                                    Text(lastW, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }

                        if (weeklyHistory.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                                    Text(
                                        text = "هنوز تاریخچه پیشرفتی ثبت نشده است",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "پس از پایان اولین هفته، روند پیشرفت فیزیکی و تمرینی شما در این بخش نمایش داده خواهد شد.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                weeklyHistory.forEachIndexed { index, item ->
                                    val prevItem = if (index > 0) weeklyHistory[index - 1] else null
                                    val weightDelta = if (prevItem != null) item.weight - prevItem.weight else null
                                    val waistDelta = if (prevItem != null) item.waist - prevItem.waist else null

                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Text(
                                                        "هفته ${item.week}",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                    )
                                                }
                                                Text(item.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text("وزن: ${item.weight} kg", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                                        if (weightDelta != null && weightDelta != 0.0) {
                                                            val deltaText = if (weightDelta > 0) "+${String.format("%.1f", weightDelta)}" else String.format("%.1f", weightDelta)
                                                            Surface(
                                                                color = if (weightDelta < 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer,
                                                                shape = RoundedCornerShape(6.dp)
                                                            ) {
                                                                Text(
                                                                    deltaText,
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (weightDelta < 0) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text("شکم: ${item.waist} cm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        if (waistDelta != null && waistDelta != 0.0) {
                                                            val waistText = if (waistDelta > 0) "+${String.format("%.1f", waistDelta)}" else String.format("%.1f", waistDelta)
                                                            Text("($waistText)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                    if (item.jointPain.isNotBlank() && item.jointPain != "بدون درد مفصلی") {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text("درد مفصلی: ${item.jointPain}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                                    }
                                                    if (item.muscleSoreness.isNotBlank() && item.muscleSoreness != "بدون کوفتگی") {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text("کوفتگی: ${item.muscleSoreness}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                Text("${item.completionRate}% انجام", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 2: Settings & Data Reset Management
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "مدیریت داده‌های اپلیکیشن",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "در صورت تغییر برنامه یا پاکسازی کامل داده‌های برنامه تمرینی فعلی و تاریخچه‌ها می‌توانید از دکمه زیر استفاده کنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedButton(
                                    onClick = {
                                        onResetData()
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("پاکسازی و ریست کامل تمامی تمرین‌ها و تاریخچه‌ها", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectAllOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current
) {
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value))
    }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (value != textFieldValueState.text) {
            textFieldValueState = TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused && textFieldValueState.text.isNotEmpty()) {
            kotlinx.coroutines.delay(50)
            textFieldValueState = textFieldValueState.copy(
                selection = TextRange(0, textFieldValueState.text.length)
            )
        }
    }

    val resolvedTextStyle = textStyle.copy(
        textDirection = if (keyboardOptions.keyboardType == KeyboardType.Number) {
            TextDirection.Ltr
        } else {
            TextDirection.ContentOrLtr
        }
    )

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            onValueChange(newValue.text)
        },
        label = label,
        leadingIcon = leadingIcon,
        modifier = modifier.onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        },
        singleLine = singleLine,
        shape = shape,
        keyboardOptions = keyboardOptions,
        textStyle = resolvedTextStyle
    )
}
