package com.fitbyai.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitbyai.app.data.WorkoutTaskEntity

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
