package com.fitbyai.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitbyai.app.data.WorkoutTaskEntity
import com.fitbyai.app.i18n.LocalAppStrings
import com.fitbyai.app.ui.WorkoutUiState

@Composable
fun HeroProgressCard(uiState: WorkoutUiState, onOpenWeeklyReview: () -> Unit) {
    val strings = LocalAppStrings.current
    val doneCount = uiState.tasks.count { it.completed }
    val totalCount = uiState.tasks.size
    val progressFraction = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f
    val progressPercent = (progressFraction * 100).toInt()
    val allCompleted = totalCount > 0 && doneCount == totalCount
    val currentWeekNumber = uiState.history.size + 1
    val remainingMs = if (uiState.deadlineTimestamp != null) uiState.deadlineTimestamp - System.currentTimeMillis() else 0L
    val isTimeRemaining = uiState.deadlineTimestamp != null && remainingMs > 0

    // Muscle volume map for integrated volume display
    val muscleVolumeMap = remember(uiState.tasks) {
        val uniqueExercises = uiState.tasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
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
                            strings.weekCompletedCelebration(currentWeekNumber),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (isTimeRemaining) {
                            Text(
                                strings.timeRemainingFreeText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        } else {
                            Text(
                                strings.endOfWeekPromptNotice,
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
                                Text(strings.getNewWeekPlanBtn, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Week Hero Card (Purple / primaryContainer)
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
                                strings.weekN(currentWeekNumber),
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
                            text = strings.setsProgress(doneCount, totalCount),
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
                            text = strings.weeklyWorkoutProgress,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$progressPercent%",
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

                // INTEGRATED MUSCLE VOLUME DISTRIBUTION SECTION (Moved into this card as requested)
                if (muscleVolumeMap.isNotEmpty()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = strings.muscleDistributionTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = strings.scientificVolumeBadge,
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        firstRow.forEach { (muscle, sets) ->
                            IntegratedMuscleSetChip(
                                muscle = strings.localizedMuscle(muscle),
                                sets = sets,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (secondRow.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            secondRow.forEach { (muscle, sets) ->
                                IntegratedMuscleSetChip(
                                    muscle = strings.localizedMuscle(muscle),
                                    sets = sets,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntegratedMuscleSetChip(muscle: String, sets: Int, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current
    val isOptimal = sets in 4..22
    Surface(
        modifier = modifier,
        color = if (isOptimal) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
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
                text = strings.setsCount(sets),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOptimal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DeadlineChip(deadlineTimestamp: Long?, tasks: List<WorkoutTaskEntity>) {
    val strings = LocalAppStrings.current
    if (deadlineTimestamp == null) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                strings.activeWeekBadge,
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
            val timeText = if (days > 0) strings.deadlineRemaining(days, remHours) else strings.deadlineHoursOnly(hours)
            Triple(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                MaterialTheme.colorScheme.onSurface,
                timeText
            )
        }
        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            strings.deadlineEnded
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
