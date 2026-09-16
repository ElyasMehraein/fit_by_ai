package com.fitbyai.app.ui.dialogs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitbyai.app.data.UserProfileEntity
import com.fitbyai.app.data.WeeklyHistoryEntity
import com.fitbyai.app.i18n.AppLanguage
import com.fitbyai.app.i18n.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    currentProfile: UserProfileEntity?,
    weeklyHistory: List<WeeklyHistoryEntity> = emptyList(),
    currentLanguage: AppLanguage = AppLanguage.DEFAULT,
    onLanguageChanged: (AppLanguage) -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (
        height: String, age: String, gender: String, goal: String,
        baseWeight: String, baseWaist: String, experience: String,
        daysPerWeek: String, equipment: String, limitations: String,
        targetWeight: String, sessionDuration: String, activityLevel: String, healthConditions: String
    ) -> Unit,
    onResetData: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    var height by remember { mutableStateOf(currentProfile?.height ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age ?: "") }
    var gender by remember { mutableStateOf(currentProfile?.gender?.ifEmpty { "male" } ?: "male") }
    var goal by remember { mutableStateOf(currentProfile?.goal ?: "Hypertrophy") }
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
    var activityLevel by remember { mutableStateOf(currentProfile?.activityLevel ?: "Sedentary") }
    var healthConditions by remember { mutableStateOf(currentProfile?.healthConditions ?: "") }

    var selectedTab by remember { mutableStateOf(0) } // 0: Specs Form, 1: Progress History, 2: Settings & Reset
    var showResetConfirm by remember { mutableStateOf(false) }

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
                        text = strings.profileTitle,
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
                                strings.tabSpecs,
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
                                strings.tabHistory,
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
                                strings.tabSettings,
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
                        text = strings.profileIntroNotice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // Gender Selection Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.genderLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val isMale = gender == "male" || gender == "مرد"
                        val isFemale = gender == "female" || gender == "زن"

                        FilterChip(
                            selected = isMale,
                            onClick = { gender = "male" },
                            label = { Text(strings.genderMale, modifier = Modifier.padding(vertical = 4.dp)) },
                            leadingIcon = if (isMale) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        FilterChip(
                            selected = isFemale,
                            onClick = { gender = "female" },
                            label = { Text(strings.genderFemale, modifier = Modifier.padding(vertical = 4.dp)) },
                            leadingIcon = if (isFemale) {
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
                        label = { Text(strings.heightLabel) },
                        leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    SelectAllOutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text(strings.ageLabel) },
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
                        label = { Text(strings.baseWeightLabel) },
                        leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    SelectAllOutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        label = { Text(strings.targetWeightLabel) },
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
                        label = { Text(strings.baseWaistLabel) },
                        leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    SelectAllOutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text(strings.daysPerWeekLabel) },
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
                    label = { Text(strings.goalLabel) },
                    leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectAllOutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text(strings.experienceLabel) },
                        leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    SelectAllOutlinedTextField(
                        value = sessionDuration,
                        onValueChange = { sessionDuration = it },
                        label = { Text(strings.sessionDurationLabel) },
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.activityLevelLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = listOf(
                            strings.activitySedentary,
                            strings.activityModerate,
                            strings.activityActive
                        )
                        options.forEach { optionText ->
                            val isSelected = activityLevel == optionText || (activityLevel.isNotBlank() && optionText.contains(activityLevel.take(4)))
                            FilterChip(
                                selected = isSelected,
                                onClick = { activityLevel = optionText },
                                label = { Text(optionText.take(12), style = MaterialTheme.typography.labelSmall) },
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
                    label = { Text(strings.equipmentLabel) },
                    leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                SelectAllOutlinedTextField(
                    value = limitations,
                    onValueChange = { limitations = it },
                    label = { Text(strings.limitationsLabel) },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                SelectAllOutlinedTextField(
                    value = healthConditions,
                    onValueChange = { healthConditions = it },
                    label = { Text(strings.healthConditionsLabel) },
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
                        strings.saveProfileBtn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (selectedTab == 1) {
                // TAB 1: Workout Progress History
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
                                Text(strings.tabHistory, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${weeklyHistory.size} ${strings.weekN(weeklyHistory.size)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
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
                                Text(strings.baseWeightLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
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
                                    text = strings.emptyHistoryNotice,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            weeklyHistory.forEachIndexed { index, item ->
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
                                                    strings.weekN(item.week),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                            Text(item.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Text(
                                            strings.historyWeightWaist(item.weight, item.waist),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            strings.historySetsCompleted(item.completedSets, item.totalSets, item.completionRate),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 2: Settings, Language Selection & Data Reset Management
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Language Selection Section
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
                                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = strings.languageSectionTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = strings.languageSectionDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Language Chips Grid
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val languages = AppLanguage.entries.toList()
                                languages.chunked(2).forEach { rowLanguages ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowLanguages.forEach { lang ->
                                            val isSelected = lang == currentLanguage
                                            Surface(
                                                onClick = { onLanguageChanged(lang) },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                                border = BorderStroke(
                                                    if (isSelected) 2.dp else 1.dp,
                                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(lang.flag, fontSize = 20.sp)
                                                    Text(
                                                        text = lang.nativeName,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Data Reset Section
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
                                Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(
                                    text = strings.resetDataTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = strings.resetDataDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedButton(
                                onClick = { showResetConfirm = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.resetDataBtn, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(strings.resetConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(strings.resetConfirmMsg) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.confirmResetBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(strings.cancelBtn)
                }
            }
        )
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
