package com.fitbyai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fitbyai.app.data.ExerciseImageHelper
import com.fitbyai.app.data.WorkoutTaskEntity
import com.fitbyai.app.ui.dialogs.GoogleImageSearchDialog

data class TaskGroup(
    val key: String,
    val tasks: List<WorkoutTaskEntity>
)

@Composable
fun StackedExerciseTaskCard(
    taskGroup: TaskGroup,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit,
    onUpdateImage: (exerciseId: String, title: String, imageUrl: String) -> Unit
) {
    val activeTask = taskGroup.tasks.firstOrNull() ?: return
    val remainingSetsCount = taskGroup.tasks.size
    val context = LocalContext.current
    var showSearchDialog by remember { mutableStateOf(false) }

    val peekCount = (remainingSetsCount - 1).coerceAtMost(3)

    if (showSearchDialog) {
        GoogleImageSearchDialog(
            exerciseTitle = activeTask.title,
            onDismiss = { showSearchDialog = false },
            onImageSelected = { imageUrl ->
                onUpdateImage(activeTask.exerciseId, activeTask.title, imageUrl)
            }
        )
    }

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

                        if (activeTask.targetPerSet.isNotBlank() || activeTask.targetMuscle.isNotBlank()) {
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

                    // Exercise Image Banner
                    val exerciseImageUrl = remember(activeTask.images, activeTask.exerciseId, activeTask.title) {
                        ExerciseImageHelper.getExerciseImageUrl(activeTask.exerciseId, activeTask.title, activeTask.images)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showSearchDialog = true }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(exerciseImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = activeTask.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Top-Right Change Photo Badge
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تغییر عکس",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "تغییر عکس",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
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

                    // Search & Pick Image Button
                    OutlinedButton(
                        onClick = { showSearchDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجوی عکس در گوگل",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جستجو و انتخاب عکس از گوگل",
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
    onDeleteTask: (taskId: String) -> Unit,
    onUpdateImage: (exerciseId: String, title: String, imageUrl: String) -> Unit
) {
    val context = LocalContext.current
    var showSearchDialog by remember { mutableStateOf(false) }

    if (showSearchDialog) {
        GoogleImageSearchDialog(
            exerciseTitle = task.title,
            onDismiss = { showSearchDialog = false },
            onImageSelected = { imageUrl ->
                onUpdateImage(task.exerciseId, task.title, imageUrl)
            }
        )
    }

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

                    if (task.targetPerSet.isNotBlank() || task.targetMuscle.isNotBlank()) {
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

                // Exercise Image Banner
                val exerciseImageUrl = remember(task.images, task.exerciseId, task.title) {
                    ExerciseImageHelper.getExerciseImageUrl(task.exerciseId, task.title, task.images)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showSearchDialog = true }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(exerciseImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = task.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Top-Right Change Photo Badge
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تغییر عکس",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "تغییر عکس",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
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

                // Search & Pick Image Button
                OutlinedButton(
                    onClick = { showSearchDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجوی عکس در گوگل",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "جستجو و انتخاب عکس از گوگل",
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
