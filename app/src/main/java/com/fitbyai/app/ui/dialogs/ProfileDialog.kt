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
import com.fitbyai.app.data.UserProfileEntity
import com.fitbyai.app.data.ProfileHistoryEntity
import com.fitbyai.app.data.getRelativeTimeSpanString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    currentProfile: UserProfileEntity?,
    profileHistory: List<ProfileHistoryEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        height: String, age: String, gender: String, goal: String,
        baseWeight: String, baseWaist: String, experience: String,
        daysPerWeek: String, equipment: String, limitations: String,
        targetWeight: String, sessionDuration: String, activityLevel: String, healthConditions: String
    ) -> Unit
) {
    var height by remember { mutableStateOf(currentProfile?.height ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age ?: "") }
    var gender by remember { mutableStateOf(currentProfile?.gender?.ifEmpty { "مرد" } ?: "مرد") }
    var goal by remember { mutableStateOf(currentProfile?.goal ?: "عضله‌سازی (حجم)") }
    var baseWeight by remember { mutableStateOf(currentProfile?.baseWeight ?: "") }
    var baseWaist by remember { mutableStateOf(currentProfile?.baseWaist ?: "") }
    var experience by remember { mutableStateOf(currentProfile?.experience ?: "متوسط (6 ماه تا 2 سال)") }
    var daysPerWeek by remember { mutableStateOf(currentProfile?.daysPerWeek ?: "4 روز در هفته") }
    var equipment by remember { mutableStateOf(currentProfile?.equipment ?: "") }
    var limitations by remember { mutableStateOf(currentProfile?.limitations ?: "") }
    var targetWeight by remember { mutableStateOf(currentProfile?.targetWeight ?: "") }
    var sessionDuration by remember { mutableStateOf(currentProfile?.sessionDuration ?: "60 دقیقه") }
    var activityLevel by remember { mutableStateOf(currentProfile?.activityLevel ?: "کم‌تحرک (کارمندی)") }
    var healthConditions by remember { mutableStateOf(currentProfile?.healthConditions ?: "") }

    var selectedTab by remember { mutableStateOf(0) } // 0: Form, 1: History

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
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "تاریخچه مشخصات (${profileHistory.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                }
            }

                if (selectedTab == 0) {
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
                            label = { Text("دور کمر (cm)") },
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
                            singleLine = true
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

                    SelectAllOutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("سابقه تمرین") },
                        leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    SelectAllOutlinedTextField(
                        value = sessionDuration,
                        onValueChange = { sessionDuration = it },
                        label = { Text("زمان هر جلسه تمرین (دقیقه)") },
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    // Daily Activity Level Selection Section (FilterChips)
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
                } else {
                    // History Tab
                    if (profileHistory.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "هنوز تاریخچه‌ای ثبت نشده است.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "با هر بار ویرایش و تغییر مشخصات پروفایل، نسخه قبلی آن همراه با زمان ثبت ذخیره خواهد شد.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "نسخه‌های قبلی پروفایل شما:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        profileHistory.forEach { item ->
                            val relativeTimeStr = getRelativeTimeSpanString(item.timestamp)
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                Text(
                                                    text = relativeTimeStr,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        Text(
                                            text = "جنسیت: ${item.gender.ifEmpty { "مرد" }}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    Text(
                                        text = "قد: ${item.height.ifEmpty { "-" }} سانتی‌متر | سن: ${item.age.ifEmpty { "-" }} | وزن: ${item.baseWeight.ifEmpty { "-" }} کیلوگرم | دور کمر: ${item.baseWaist.ifEmpty { "-" }} سانتی‌متر",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (item.targetWeight.isNotEmpty()) {
                                        Text(
                                            text = "وزن هدف: ${item.targetWeight} کیلوگرم",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "هدف: ${item.goal} | سابقه: ${item.experience}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (item.equipment.isNotEmpty()) {
                                        Text(
                                            text = "تجهیزات: ${item.equipment}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (item.limitations.isNotEmpty()) {
                                        Text(
                                            text = "محدودیت‌ها: ${item.limitations}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
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
    var textFieldValueState by remember(value) {
        mutableStateOf(TextFieldValue(text = value))
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newValue ->
            textFieldValueState = newValue
            onValueChange(newValue.text)
        },
        label = label,
        leadingIcon = leadingIcon,
        modifier = modifier.onFocusChanged { focusState ->
            if (focusState.isFocused && textFieldValueState.text.isNotEmpty()) {
                textFieldValueState = textFieldValueState.copy(
                    selection = TextRange(0, textFieldValueState.text.length)
                )
            }
        },
        singleLine = singleLine,
        shape = shape,
        keyboardOptions = keyboardOptions,
        textStyle = textStyle
    )
}
