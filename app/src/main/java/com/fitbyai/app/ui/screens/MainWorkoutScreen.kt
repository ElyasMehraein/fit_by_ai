package com.fitbyai.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fitbyai.app.data.WorkoutTaskEntity
import com.fitbyai.app.ui.WorkoutUiState
import com.fitbyai.app.ui.WorkoutViewModel
import com.fitbyai.app.ui.dialogs.HistoryDialog
import com.fitbyai.app.ui.dialogs.ProfileDialog
import com.fitbyai.app.ui.dialogs.WeeklyReviewDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWorkoutScreen(viewModel: WorkoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fit by AI",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = { showHistoryDialog = true }) {
                        Icon(Icons.Default.History, contentDescription = "تاریخچه", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {
                        if (uiState.userProfile == null) {
                            showProfileDialog = true
                        } else {
                            showReviewDialog = true
                        }
                    }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "بروزرسانی", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = uiState.selectedTab == "queue",
                    onClick = { viewModel.setSelectedTab("queue") },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text("تمرینات", style = MaterialTheme.typography.labelMedium) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showHistoryDialog = true },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = null) },
                    label = { Text("تاریخچه", style = MaterialTheme.typography.labelMedium) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showProfileDialog = true },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("پروفایل", style = MaterialTheme.typography.labelMedium) }
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
            // Deadline Countdown Banner
            DeadlineBanner(uiState.deadlineTimestamp, uiState.tasks)

            // Progress Bar Card
            val doneCount = uiState.tasks.count { it.completed }
            val totalCount = uiState.tasks.size
            val progressPercent = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100).toInt() else 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("پیشرفت", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$progressPercent٪", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) doneCount.toFloat() / totalCount else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TabButton(
                    title = "در صف انجام (${uiState.tasks.count { !it.completed }})",
                    selected = uiState.selectedTab == "queue",
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.setSelectedTab("queue")
                }
                TabButton(
                    title = "انجام شده (${uiState.tasks.count { it.completed }})",
                    selected = uiState.selectedTab == "done",
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.setSelectedTab("done")
                }
            }

            // Task List
            val displayList = if (uiState.selectedTab == "queue") {
                uiState.tasks.filter { !it.completed }
            } else {
                uiState.tasks.filter { it.completed }
            }

            if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("برنامه‌ای تعریف نشده است", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Text("اطلاعات خود را وارد کنید تا برنامه شخصی‌سازی شده دریافت کنید", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { if (uiState.userProfile == null) showProfileDialog = true else showReviewDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("دریافت برنامه جدید")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayList, key = { it.taskId }) { task ->
                        TaskCard(task = task, onToggleStatus = {
                            viewModel.toggleTaskCompletion(task.taskId, task.completed)
                        })
                    }
                }
            }
        }
    }

    // Dialogs
    if (showProfileDialog) {
        ProfileDialog(
            currentProfile = uiState.userProfile,
            onDismiss = { showProfileDialog = false },
            onSave = { h, a, g, goal, bw, bwa, exp, days, eq, lim ->
                viewModel.saveProfile(h, a, g, goal, bw, bwa, exp, days, eq, lim)
            }
        )
    }

    if (showReviewDialog) {
        val lastWeight = uiState.history.lastOrNull()?.weight?.toString() ?: uiState.userProfile?.baseWeight ?: ""
        val lastWaist = uiState.history.lastOrNull()?.waist?.toString() ?: uiState.userProfile?.baseWaist ?: ""
        
        WeeklyReviewDialog(
            generatedPrompt = uiState.generatedPrompt,
            errorMessage = uiState.errorMessage,
            isLoading = uiState.isLoading,
            initialWeight = lastWeight,
            initialWaist = lastWaist,
            onDismiss = { showReviewDialog = false },
            onEditProfile = {
                showReviewDialog = false
                showProfileDialog = true
            },
            onGeneratePrompt = { w, wa, s, e, rpe, pain, fb ->
                viewModel.generatePrompt(w, wa, s, e, rpe, pain, fb)
            },
            onImportProgram = { raw, w, wa, s, e, rpe, pain, fb ->
                viewModel.importProgram(raw, w, wa, s, e, rpe, pain, fb) {
                    showReviewDialog = false
                }
            }
        )
    }

    if (showHistoryDialog) {
        HistoryDialog(
            historyList = uiState.history,
            onDismiss = { showHistoryDialog = false },
            onResetData = {
                viewModel.resetAllData()
                showHistoryDialog = false
            }
        )
    }
}

@Composable
fun TaskCard(task: WorkoutTaskEntity, onToggleStatus: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Exercise Image
            if (task.images.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(task.images.first())
                        .crossfade(true)
                        .build(),
                    contentDescription = task.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "ست ${task.setNumber} از ${task.totalSets}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.completed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (task.completed) "↺ بازگردانی" else "✓ انجام شد",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(task.exerciseId, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun TabButton(title: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.size(24.dp, 2.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
            }
        }
    }
}

@Composable
fun DeadlineBanner(deadlineTimestamp: Long?, tasks: List<WorkoutTaskEntity>) {
    if (deadlineTimestamp == null) return

    val remaining = deadlineTimestamp - System.currentTimeMillis()
    val allCompleted = tasks.isNotEmpty() && tasks.all { it.completed }

    val (bgColor, textColor, message) = when {
        allCompleted -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary, "تمام تمرینات این هفته انجام شد")
        remaining > 0 -> {
            val hours = (remaining / (1000 * 60 * 60))
            val days = hours / 24
            Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "$days روز و ${hours % 24} ساعت تا پایان هفته")
        }
        else -> Triple(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), MaterialTheme.colorScheme.error, "زمان این هفته به پایان رسیده است")
    }

    Surface(
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 10.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
            Text(message, style = MaterialTheme.typography.labelMedium, color = textColor)
        }
    }
}
