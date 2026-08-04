package com.fitbyai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fitbyai.app.data.WorkoutTaskEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskCard(
    task: WorkoutTaskEntity,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit,
    content: @Composable () -> Unit
) {
    // Isolated key ensures each task has a completely fresh dismissState starting at Settled.
    // When a task is deleted or completed, the next task in queue won't inherit a stuck dismiss state.
    key(task.taskId) {
        // Force LTR for the SwipeToDismissBox gesture engine so physical drag direction
        // matches physical screen movement 1:1 without RTL inversion or sticking bugs.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val dismissState = rememberSwipeToDismissBoxState(
                positionalThreshold = { totalDistance -> totalDistance * 0.75f },
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
                            false
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
                                        colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF4CAF50))
                                    )
                                    isLeftSwipeDelete -> Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFEF5350), Color(0xFFC62828), Color(0xFF8E0000))
                                    )
                                    else -> Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.Transparent))
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
                        } else if (isLeftSwipeDelete) {
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
                },
                content = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        content()
                    }
                }
            )
        }
    }
}
