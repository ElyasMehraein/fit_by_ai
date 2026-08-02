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
                    Column {
                        Text(
                            text = "Fit by AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مدیریت برنامه و ریکاوری هفتگی",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = uiState.selectedTab == "queue",
                    onClick = { viewModel.setSelectedTab("queue") },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text("تمرینات", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showHistoryDialog = true },
                    icon = { Icon(Icons.Default.ShowChart, contentDescription = null) },
                    label = { Text("تاریخچه", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showProfileDialog = true },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("پروفایل", fontSize = 10.sp) }
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
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("پیشرفت هفته جاری", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("$progressPercent٪", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) doneCount.toFloat() / totalCount else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("برنامه‌ای تعریف نشده است", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("از دکمه بالا برای بارگذاری JSON استفاده کنید", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            if (uiState.userProfile == null) showProfileDialog = true else showReviewDialog = true
                        }) {
                            Text("دریافت و بارگذاری برنامه جدید")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
        WeeklyReviewDialog(
            generatedPrompt = uiState.generatedPrompt,
            errorMessage = uiState.errorMessage,
            isLoading = uiState.isLoading,
            onDismiss = { showReviewDialog = false },
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        .height(170.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(task.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "ست ${task.setNumber} از ${task.totalSets}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(task.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onToggleStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.completed) Color(0x33FBBF24) else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (task.completed) "↺ بازگردانی به صف" else "✓ انجام شد",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.completed) Color(0xFFFBBF24) else Color(0xFF020617)
                        )
                    }
                    Text(task.exerciseId, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DeadlineBanner(deadlineTimestamp: Long?, tasks: List<WorkoutTaskEntity>) {
    if (deadlineTimestamp == null) return

    val remaining = deadlineTimestamp - System.currentTimeMillis()
    val allCompleted = tasks.isNotEmpty() && tasks.all { it.completed }

    val (bgColor, textColor, message) = when {
        allCompleted -> Triple(Color(0x2210B981), Color(0xFF34D399), "تمام حرکات این هفته رو انجام دادی! 👏")
        remaining > 0 -> {
            val hours = (remaining / (1000 * 60 * 60))
            val days = hours / 24
            Triple(Color(0x22FBBF24), Color(0xFFFBBF24), "$days روز و ${hours % 24} ساعت فرصت باقی‌مانده")
        }
        else -> Triple(Color(0x22FB7185), Color(0xFFFB7185), "تاخیر در انجام حرکات هفتگی!")
    }

    Surface(
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.HourglassHalf, contentDescription = null, tint = textColor)
            Text(message, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}
