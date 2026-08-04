package com.fitbyai.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fitbyai.app.R
import com.fitbyai.app.ui.WorkoutViewModel
import com.fitbyai.app.ui.components.*
import com.fitbyai.app.ui.dialogs.ManualProgramDialog
import com.fitbyai.app.ui.dialogs.ProfileDialog
import com.fitbyai.app.ui.dialogs.WeeklyReviewDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWorkoutScreen(viewModel: WorkoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showManualProgramDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
                                    text = "Fit by AI",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "برنامه‌ریزی و مربی هوشمند تمرین",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
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
                        label = { Text("تمرینات", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
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
                        label = { Text("دستیار AI", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showManualProgramDialog = true },
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                        label = { Text("برنامه دستی", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showProfileDialog = true },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("پروفایل", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
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
                if (uiState.tasks.isNotEmpty()) {
                    // Segmented Tab Controls pinned at top for fast navigation
                    M3SegmentedTabRow(
                        selectedTab = uiState.selectedTab,
                        queueCount = uiState.tasks.count { !it.completed },
                        doneCount = uiState.tasks.count { it.completed },
                        onTabSelected = { viewModel.setSelectedTab(it) }
                    )
                }

                val queuedGroups = remember(uiState.tasks) {
                    uiState.tasks
                        .filter { !it.completed }
                        .groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
                        .map { (key, groupTasks) -> TaskGroup(key, groupTasks.sortedBy { it.setNumber }) }
                }

                val doneTasks = remember(uiState.tasks) {
                    uiState.tasks.filter { it.completed }
                }

                val isQueueTab = uiState.selectedTab == "queue"

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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item(key = "hero_progress") {
                            HeroProgressCard(
                                uiState = uiState,
                                onOpenWeeklyReview = {
                                    if (uiState.userProfile == null) showProfileDialog = true else showReviewDialog = true
                                }
                            )
                        }

                        item(key = "muscle_volume") {
                            MuscleVolumeDashboardCard(tasks = uiState.tasks)
                        }

                        if ((isQueueTab && queuedGroups.isEmpty()) || (!isQueueTab && doneTasks.isEmpty())) {
                            item(key = "empty_tab_notice") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isQueueTab) "هیچ تمرینی در صف انجام نیست! 🎉" else "هنوز تمرینی را به‌طور کامل به پایان نرسانده‌اید.",
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
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modal Bottom Sheets / Dialogs
        if (showProfileDialog) {
            ProfileDialog(
                currentProfile = uiState.userProfile,
                weeklyHistory = uiState.history,
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
                    com.fitbyai.app.data.getRelativeTimeSpanString(lastTimestamp)
                } else {
                    "همین الان"
                }
            }

            WeeklyReviewDialog(
                generatedPrompt = uiState.generatedPrompt,
                errorMessage = uiState.errorMessage,
                isLoading = uiState.isLoading,
                initialWeight = lastWeight,
                initialWaist = lastWaist,
                profileLastUpdatedText = lastProfileUpdateStr,
                onDismiss = { showReviewDialog = false },
                onEditProfile = {
                    showReviewDialog = false
                    showProfileDialog = true
                },
                onGeneratePrompt = { w, wa, s, e, rpe, pain, fb, ms, jp ->
                    viewModel.generatePrompt(w, wa, s, e, rpe, pain, fb, ms, jp)
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
}
