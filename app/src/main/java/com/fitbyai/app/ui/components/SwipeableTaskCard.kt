package com.fitbyai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fitbyai.app.data.WorkoutTaskEntity
import com.fitbyai.app.i18n.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskCard(
    task: WorkoutTaskEntity,
    onToggleStatus: (taskId: String, currentStatus: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit,
    content: @Composable () -> Unit
) {
    val strings = LocalAppStrings.current
    val currentDirection = LocalLayoutDirection.current

    // Isolated key ensures each task has a completely fresh dismissState starting at Settled.
    key(task.taskId) {
        // Force LTR for the SwipeToDismissBox gesture engine so physical drag direction
        // matches physical screen movement 1:1 without RTL inversion or sticking bugs.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val density = LocalDensity.current
                val cardWidthPx = with(density) { maxWidth.toPx() }
                // Require physical drag across at least 50% (half) of the screen width
                val minRequiredDistancePx = cardWidthPx * 0.50f

                var dismissStateRef by remember(task.taskId) { mutableStateOf<SwipeToDismissBoxState?>(null) }

                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { minRequiredDistancePx },
                    confirmValueChange = { dismissValue ->
                        val actualOffsetPx = dismissStateRef?.let {
                            try {
                                kotlin.math.abs(it.requireOffset())
                            } catch (e: Exception) {
                                0f
                            }
                        } ?: 0f

                        // STRICT PHYSICAL DISTANCE CHECK:
                        // Ignore quick finger flings/throws if physical drag distance is less than 50% of screen width.
                        if (actualOffsetPx < minRequiredDistancePx) {
                            false
                        } else {
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
                    }
                )
                dismissStateRef = dismissState

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
                                CompositionLocalProvider(LocalLayoutDirection provides currentDirection) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = strings.markComplete,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = strings.markComplete,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else if (isLeftSwipeDelete) {
                                CompositionLocalProvider(LocalLayoutDirection provides currentDirection) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = strings.deleteSet,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = strings.deleteSet,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    content = {
                        CompositionLocalProvider(LocalLayoutDirection provides currentDirection) {
                            content()
                        }
                    }
                )
            }
        }
    }
}
