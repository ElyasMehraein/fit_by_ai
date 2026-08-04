package com.fitbyai.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fitbyai.app.R
import com.fitbyai.app.data.WorkoutTaskEntity
import com.fitbyai.app.ui.WorkoutUiState
import com.fitbyai.app.ui.WorkoutViewModel
import com.fitbyai.app.ui.dialogs.ManualProgramDialog
import com.fitbyai.app.ui.dialogs.ProfileDialog
import kotlinx.coroutines.launch
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

    // Modal Bottom Sheets
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

@Composable
fun HeroProgressCard(uiState: WorkoutUiState, onOpenWeeklyReview: () -> Unit) {
    val doneCount = uiState.tasks.count { it.completed }
    val totalCount = uiState.tasks.size
    val progressFraction = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f
    val progressPercent = (progressFraction * 100).toInt()
    val allCompleted = totalCount > 0 && doneCount == totalCount
    val currentWeekNumber = uiState.history.size + 1
    val remainingMs = if (uiState.deadlineTimestamp != null) uiState.deadlineTimestamp - System.currentTimeMillis() else 0L
    val isTimeRemaining = uiState.deadlineTimestamp != null && remainingMs > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // End of Week Celebration Hero Banner
        if (allCompleted) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(14.dp).size(32.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "هفته $currentWeekNumber با موفقیت تکمیل شد! 🏆",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (isTimeRemaining) {
                            Text(
                                "تا پایان یک هفته هنوز وقت داری پس هر کاری دلت میخواد انجام بده تو شایسته این آزادی هستی بعدش بیا برنامه هفته بعد رو بگیر",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        } else {
                            Text(
                                "شاخص‌های این هفته را ثبت کنید تا هوش مصنوعی برنامه جدید را بسازد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onOpenWeeklyReview,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("دریافت برنامه هفته جدید 🚀", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Week Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "هفته $currentWeekNumber",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        DeadlineChip(uiState.deadlineTimestamp, uiState.tasks)
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$doneCount از $totalCount ست",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "پیشرفت تمرینات هفته",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$progressPercent٪",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.SportsGymnastics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(28.dp)
                        )
                    }
                }

                val animatedProgress by animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                    label = "ProgressAnimation"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                )
            }
        }
    }
}

@Composable
fun DeadlineChip(deadlineTimestamp: Long?, tasks: List<WorkoutTaskEntity>) {
    if (deadlineTimestamp == null) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "برنامه هفته فعال",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    val remaining = deadlineTimestamp - System.currentTimeMillis()

    val (bgColor, textColor, text) = when {
        remaining > 0 -> {
            val hours = (remaining / (1000 * 60 * 60))
            val days = hours / 24
            val remHours = hours % 24
            val timeText = if (days > 0) "⏱️ $days روز و $remHours ساعت مابقی" else "⏱️ $hours ساعت مابقی"
            Triple(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.onSurface,
                timeText
            )
        }
        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "⏱️ پایان مهلت هفته"
        )
    }

    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun M3SegmentedTabRow(
    selectedTab: String,
    queueCount: Int,
    doneCount: Int,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SegmentedButtonOption(
                title = "در صف انجام ($queueCount)",
                selected = selectedTab == "queue",
                modifier = Modifier.weight(1f)
            ) {
                onTabSelected("queue")
            }

            SegmentedButtonOption(
                title = "تکمیل شده ($doneCount)",
                selected = selectedTab == "done",
                modifier = Modifier.weight(1f)
            ) {
                onTabSelected("done")
            }
        }
    }
}

@Composable
fun SegmentedButtonOption(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

data class TaskGroup(
    val key: String,
    val tasks: List<WorkoutTaskEntity>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskCard(
    task: WorkoutTaskEntity,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit,
    content: @Composable () -> Unit
) {
    // Force LTR for the SwipeToDismissBox gesture engine so physical drag direction
    // matches physical screen movement 1:1 without RTL inversion or sticking bugs.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                when (dismissValue) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        // Physical Swipe Right (Left -> Right) -> Complete task
                        onToggleStatus(task.taskId, task.completed)
                        false
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        // Physical Swipe Left (Right -> Left) -> Delete task
                        onDeleteTask(task.taskId)
                        true
                    }
                    SwipeToDismissBoxValue.Settled -> false
                }
            }
        )

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            backgroundContent = {
                val direction = dismissState.dismissDirection
                val isRightSwipeDone = direction == SwipeToDismissBoxValue.StartToEnd
                val isLeftSwipeDelete = direction == SwipeToDismissBoxValue.EndToStart

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when {
                                isRightSwipeDone -> Brush.horizontalGradient(
                                    colors = listOf(Color(0xEE2E7D32), Color(0x994CAF50), Color(0x3381C784))
                                )
                                isLeftSwipeDelete -> Brush.horizontalGradient(
                                    colors = listOf(Color(0x33E57373), Color(0x99E53935), Color(0xEEC62828))
                                )
                                else -> Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        content()
                    }

                    val direction = dismissState.dismissDirection
                    val isRightSwipeDone = direction == SwipeToDismissBoxValue.StartToEnd
                    val isLeftSwipeDelete = direction == SwipeToDismissBoxValue.EndToStart

                    if (isRightSwipeDone || isLeftSwipeDelete) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isRightSwipeDone) {
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0xDD1B5E20), Color(0xEE2E7D32), Color(0x884CAF50))
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0x88EF5350), Color(0xEEC62828), Color(0xDD8E0000))
                                        )
                                    }
                                )
                                .padding(horizontal = 24.dp),
                            contentAlignment = if (isRightSwipeDone) Alignment.CenterStart else Alignment.CenterEnd
                        ) {
                            if (isRightSwipeDone) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "انجام شد",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "انجام شد",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "حذف",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "حذف",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun StackedExerciseTaskCard(
    taskGroup: TaskGroup,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit
) {
    val activeTask = taskGroup.tasks.firstOrNull() ?: return
    val remainingSetsCount = taskGroup.tasks.size
    val context = LocalContext.current

    val peekCount = (remainingSetsCount - 1).coerceAtMost(3)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = (peekCount * 8).dp, start = (peekCount * 8).dp)
    ) {
        // Physical Deck Card Layers stacked behind active top card with thick distinct primary border
        for (i in peekCount downTo 1) {
            val offsetX = (-8 * i).dp
            val offsetY = (-8 * i).dp

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(defaultElevation = (2 - i).dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = offsetX, y = offsetY)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeTask.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ست ${activeTask.setNumber + i} از ${activeTask.totalSets} (در صف)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Active Top Front Card
        SwipeableTaskCard(
            task = activeTask,
            onToggleStatus = onToggleStatus,
            onDeleteTask = onDeleteTask
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (activeTask.completed) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with exercise title and remaining set badges
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeTask.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (activeTask.completed) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Layers,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = "$remainingSetsCount ست در صف",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "ست ${activeTask.setNumber} از ${activeTask.totalSets}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (activeTask.targetPerSet.isNotBlank() || activeTask.targetMuscle.isNotBlank() || activeTask.movementPattern.isNotBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (activeTask.targetMuscle.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "عضله: ${activeTask.targetMuscle}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (activeTask.movementPattern.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "الگو: ${activeTask.movementPattern}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (activeTask.targetPerSet.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "هدف: ${activeTask.targetPerSet}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (activeTask.description.isNotBlank()) {
                        Text(
                            text = activeTask.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }

                    // Search Images Button
                    OutlinedButton(
                        onClick = {
                            val searchQuery = Uri.encode("حرکت ورزشی ${activeTask.title}")
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/search?q=$searchQuery&tbm=isch")
                            )
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجوی تصاویر گوگل",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مشاهده تصاویر حرکت در گوگل",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Action Buttons Row: Delete and Complete Set
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onDeleteTask(activeTask.taskId) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف ست",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Button(
                            onClick = { onToggleStatus(activeTask.taskId, activeTask.completed) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTask.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                contentColor = if (activeTask.completed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (activeTask.completed) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (activeTask.completed) "انجام شد (تغییر وضعیت)" else if (activeTask.setNumber == activeTask.totalSets) "انجام آخرین ست" else "تکمیل ست ${activeTask.setNumber}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleSetTaskCard(
    task: WorkoutTaskEntity,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit
) {
    val context = LocalContext.current

    SwipeableTaskCard(
        task = task,
        onToggleStatus = onToggleStatus,
        onDeleteTask = onDeleteTask
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (task.completed) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (task.completed) 1.dp else 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Exercise Title & Set Badge Header
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = if (task.completed) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "ست ${task.setNumber} از ${task.totalSets}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (task.completed) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (task.targetPerSet.isNotBlank() || task.targetMuscle.isNotBlank() || task.movementPattern.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (task.targetMuscle.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "عضله: ${task.targetMuscle}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (task.movementPattern.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "الگو: ${task.movementPattern}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (task.targetPerSet.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "هدف: ${task.targetPerSet}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }

                // Search Images Button
                OutlinedButton(
                    onClick = {
                        val searchQuery = Uri.encode("حرکت ورزشی ${task.title}")
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q=$searchQuery&tbm=isch")
                        )
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجوی تصاویر گوگل",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مشاهده تصاویر حرکت در گوگل",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Action Buttons Row: Delete and Complete / Restore
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onDeleteTask(task.taskId) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "حذف ست",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Button(
                        onClick = { onToggleStatus(task.taskId, task.completed) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (task.completed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (task.completed) Icons.Default.Undo else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (task.completed) "بازگردانی به صف" else if (task.setNumber == task.totalSets) "انجام آخرین ست" else "تکمیل ست ${task.setNumber}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoProfileStateCard(onOpenProfile: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(20.dp)
                            .size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "هنوز مشخصات ورزشی ثبت نشده است",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "برای تولید برنامه تمرینی اختصاصی و هوشمند، ابتدا مشخصات ورزشی و اهداف خود را ثبت کنید.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Button(
                    onClick = onOpenProfile,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ثبت مشخصات و پروفایل ورزشی ✍️", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NoWorkoutProgramStateCard(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(20.dp)
                            .size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "هنوز برنامه‌ای تنظیم نشده است",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "پروفایل ورزشی شما ثبت شده است. اکنون می‌توانید اولین برنامه تمرینی اختصاصی خود را دریافت کنید.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Button(
                    onClick = onGetStarted,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("دریافت اولین برنامه تمرینی 🚀", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MuscleVolumeDashboardCard(tasks: List<WorkoutTaskEntity>) {
    if (tasks.isEmpty()) return

    val muscleVolumeMap = remember(tasks) {
        val uniqueExercises = tasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
        val volumeMap = mutableMapOf<String, Int>()

        uniqueExercises.forEach { (_, groupTasks) ->
            val first = groupTasks.first()
            val muscle = when {
                first.targetMuscle.isNotBlank() -> first.targetMuscle
                else -> inferTargetMuscle(first.exerciseId, first.title)
            }
            volumeMap[muscle] = (volumeMap[muscle] ?: 0) + groupTasks.size
        }
        volumeMap
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "توزیع حجم عضلانی هفتگی",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "حجم علمی بر اساس سطح",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            val entriesList = muscleVolumeMap.entries.toList()
            val firstRow = entriesList.take(4)
            val secondRow = entriesList.drop(4).take(4)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                firstRow.forEach { (muscle, sets) ->
                    MuscleSetChip(muscle = muscle, sets = sets, modifier = Modifier.weight(1f))
                }
            }
            if (secondRow.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    secondRow.forEach { (muscle, sets) ->
                        MuscleSetChip(muscle = muscle, sets = sets, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MuscleSetChip(muscle: String, sets: Int, modifier: Modifier = Modifier) {
    val isOptimal = sets in 4..22
    Surface(
        modifier = modifier,
        color = if (isOptimal) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = muscle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$sets ست",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOptimal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun inferTargetMuscle(exerciseId: String, title: String): String {
    val search = "$exerciseId $title".lowercase()
    return when {
        search.contains("squat") || search.contains("leg") || search.contains("lunge") || search.contains("calf") || search.contains("پا") -> "پا"
        search.contains("press") && search.contains("bench") || search.contains("pushup") || search.contains("chest") || search.contains("سینه") -> "سینه"
        search.contains("row") || search.contains("pull") || search.contains("lat") || search.contains("deadlift") || search.contains("پشت") -> "پشت"
        search.contains("shoulder") || search.contains("raise") || search.contains("military") || search.contains("شانه") -> "شانه"
        search.contains("curl") || search.contains("tricep") || search.contains("bicep") || search.contains("بازو") -> "بازو"
        search.contains("plank") || search.contains("crunch") || search.contains("core") || search.contains("شکم") -> "شکم"
        else -> "سایر"
    }
}
