package com.fitbyai.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitbyai.app.R
import com.fitbyai.app.i18n.AppLanguage
import com.fitbyai.app.i18n.LocalAppStrings
import com.fitbyai.app.ui.WorkoutViewModel
import com.fitbyai.app.ui.components.*
import com.fitbyai.app.ui.dialogs.LanguageSelectionDialog
import com.fitbyai.app.ui.dialogs.ManualProgramDialog
import com.fitbyai.app.ui.dialogs.ProfileDialog
import com.fitbyai.app.ui.dialogs.WeeklyReviewDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWorkoutScreen(
    viewModel: WorkoutViewModel,
    currentLanguage: AppLanguage = AppLanguage.DEFAULT,
    onLanguageChanged: (AppLanguage) -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showManualProgramDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Day Toggle Filter State (null = All Week, 1 = Day 1, 2 = Day 2, etc.)
    var selectedDayFilter by remember { mutableStateOf<Int?>(null) }

    val availableDays = remember(uiState.tasks) {
        val days = uiState.tasks.map { it.day }.distinct().sorted()
        if (days.isEmpty()) listOf(1) else days
    }

    val filteredTasks = remember(uiState.tasks, selectedDayFilter) {
        if (selectedDayFilter == null) {
            uiState.tasks
        } else {
            uiState.tasks.filter { it.day == selectedDayFilter }
        }
    }

    val queuedGroups = remember(filteredTasks) {
        filteredTasks
            .filter { !it.completed }
            .groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
            .map { (key, groupTasks) -> TaskGroup(key, groupTasks.sortedBy { it.setNumber }) }
    }

    val doneTasks = remember(filteredTasks) {
        filteredTasks.filter { it.completed }
    }

    val isQueueTab = uiState.selectedTab == "queue"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Fit by AI Logo",
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Fit
                        )
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.appSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showLanguageDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = currentLanguage.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentLanguage.code.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = uiState.selectedTab == "queue" || uiState.selectedTab == "done",
                    onClick = { viewModel.setSelectedTab("queue") },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text(strings.navWorkouts, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        if (uiState.userProfile == null) {
                            showProfileDialog = true
                        } else {
                            showReviewDialog = true
                        }
                    },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text(strings.navAiAssistant, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showManualProgramDialog = true },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    label = { Text(strings.navManualProgram, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showProfileDialog = true },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(strings.navProfile, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.tasks.isEmpty()) {
                if (uiState.userProfile == null) {
                    NoProfileStateCard(
                        onOpenProfile = { showProfileDialog = true }
                    )
                } else {
                    NoWorkoutProgramStateCard(
                        onGetStarted = { showReviewDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Purple Hero Progress Card (with integrated Muscle Volume Distribution)
                    item(key = "hero_progress") {
                        HeroProgressCard(
                            uiState = uiState,
                            onOpenWeeklyReview = {
                                if (uiState.userProfile == null) showProfileDialog = true else showReviewDialog = true
                            }
                        )
                    }

                    // 2. Day Toggle Selector Row [ All Week | Day 1 | Day 2 | ... ]
                    item(key = "day_toggle_row") {
                        WorkoutDayToggleRow(
                            selectedDay = selectedDayFilter,
                            availableDays = availableDays,
                            onSelectDay = { selectedDayFilter = it }
                        )
                    }

                    // 3. In Queue & Completed Tabs - RELOCATED DIRECTLY ABOVE THE EXERCISE SLIDES
                    item(key = "segmented_tabs") {
                        M3SegmentedTabRow(
                            selectedTab = uiState.selectedTab,
                            queueCount = filteredTasks.count { !it.completed },
                            doneCount = filteredTasks.count { it.completed },
                            onTabSelected = { viewModel.setSelectedTab(it) }
                        )
                    }

                    // 4. Exercise Task Slides / Cards
                    if ((isQueueTab && queuedGroups.isEmpty()) || (!isQueueTab && doneTasks.isEmpty())) {
                        item(key = "empty_tab_notice") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isQueueTab) strings.noQueueTasks else strings.noDoneTasks,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (isQueueTab) {
                        items(queuedGroups, key = { it.key }) { group ->
                            if (group.tasks.size == 1) {
                                SingleSetTaskCard(
                                    task = group.tasks.first(),
                                    onToggleStatus = { taskId, currentStatus ->
                                        viewModel.toggleTaskCompletion(taskId, currentStatus)
                                    },
                                    onDeleteTask = { taskId ->
                                        viewModel.deleteTask(taskId)
                                    },
                                    onUpdateImage = { exerciseId, title, imageUrl ->
                                        viewModel.updateExerciseImage(exerciseId, title, imageUrl)
                                    }
                                )
                            } else {
                                StackedExerciseTaskCard(
                                    taskGroup = group,
                                    onToggleStatus = { taskId, currentStatus ->
                                        viewModel.toggleTaskCompletion(taskId, currentStatus)
                                    },
                                    onDeleteTask = { taskId ->
                                        viewModel.deleteTask(taskId)
                                    },
                                    onUpdateImage = { exerciseId, title, imageUrl ->
                                        viewModel.updateExerciseImage(exerciseId, title, imageUrl)
                                    }
                                )
                            }
                        }
                    } else {
                        items(doneTasks, key = { it.taskId }) { task ->
                            SingleSetTaskCard(
                                task = task,
                                onToggleStatus = { taskId, currentStatus ->
                                    viewModel.toggleTaskCompletion(taskId, currentStatus)
                                },
                                onDeleteTask = { taskId ->
                                    viewModel.deleteTask(taskId)
                                },
                                onUpdateImage = { exerciseId, title, imageUrl ->
                                    viewModel.updateExerciseImage(exerciseId, title, imageUrl)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets & Dialogs
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageChanged,
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showProfileDialog) {
        ProfileDialog(
            currentProfile = uiState.userProfile,
            weeklyHistory = uiState.history,
            currentLanguage = currentLanguage,
            onLanguageChanged = onLanguageChanged,
            onDismiss = { showProfileDialog = false },
            onSave = { h, a, g, goal, bw, bwa, exp, days, eq, lim, tw, dur, act, hc ->
                viewModel.saveProfile(h, a, g, goal, bw, bwa, exp, days, eq, lim, tw, dur, act, hc)
            },
            onResetData = {
                viewModel.resetAllData()
                showProfileDialog = false
            }
        )
    }

    if (showReviewDialog) {
        val lastWeight = uiState.history.lastOrNull()?.weight?.toString() ?: uiState.userProfile?.baseWeight ?: ""
        val lastWaist = uiState.history.lastOrNull()?.waist?.toString() ?: uiState.userProfile?.baseWaist ?: ""
        val lastProfileUpdateStr = remember(uiState.profileHistory) {
            val lastTimestamp = uiState.profileHistory.firstOrNull()?.timestamp
            if (lastTimestamp != null) {
                com.fitbyai.app.data.getRelativeTimeSpanString(lastTimestamp, strings)
            } else {
                strings.timeJustNow
            }
        }

        WeeklyReviewDialog(
            generatedPrompt = uiState.generatedPrompt,
            errorMessage = uiState.errorMessage,
            isLoading = uiState.isLoading,
            initialWeight = lastWeight,
            initialWaist = lastWaist,
            profileLastUpdatedText = lastProfileUpdateStr,
            currentLanguage = currentLanguage,
            onDismiss = { showReviewDialog = false },
            onEditProfile = {
                showReviewDialog = false
                showProfileDialog = true
            },
            onGeneratePrompt = { w, wa, s, e, rpe, pain, fb, ms, jp ->
                viewModel.generatePrompt(w, wa, s, e, rpe, pain, fb, ms, jp, currentLanguage)
            },
            onImportProgram = { raw, w, wa, s, e, rpe, pain, fb, ms, jp ->
                viewModel.importProgram(raw, w, wa, s, e, rpe, pain, fb, ms, jp) {
                    showReviewDialog = false
                }
            }
        )
    }

    if (showManualProgramDialog) {
        val lastWeight = uiState.history.lastOrNull()?.weight?.toString() ?: uiState.userProfile?.baseWeight ?: ""
        val lastWaist = uiState.history.lastOrNull()?.waist?.toString() ?: uiState.userProfile?.baseWaist ?: ""
        ManualProgramDialog(
            initialWeight = lastWeight,
            initialWaist = lastWaist,
            onDismiss = { showManualProgramDialog = false },
            onSaveProgram = { raw, w, wa, s, e, rpe, pain, fb ->
                viewModel.importProgram(raw, w, wa, s, e, rpe, pain, fb) {
                    showManualProgramDialog = false
                }
            }
        )
    }
}
