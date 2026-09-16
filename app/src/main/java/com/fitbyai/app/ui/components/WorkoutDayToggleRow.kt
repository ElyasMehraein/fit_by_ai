package com.fitbyai.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitbyai.app.i18n.LocalAppStrings

@Composable
fun WorkoutDayToggleRow(
    selectedDay: Int?,
    availableDays: List<Int>,
    onSelectDay: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    val daysList = if (availableDays.isEmpty()) listOf(1) else availableDays

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All Week" option
            item(key = "day_all") {
                val isAllSelected = selectedDay == null
                val bgColor by animateColorAsState(
                    targetValue = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    label = "allWeekBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "allWeekText"
                )

                Surface(
                    onClick = { onSelectDay(null) },
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    tonalElevation = if (isAllSelected) 3.dp else 0.dp,
                    border = if (!isAllSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) else null
                ) {
                    Text(
                        text = strings.allWeek,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            // Individual Days options: Day 1, Day 2, ...
            items(daysList, key = { it }) { day ->
                val isSelected = selectedDay == day
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    label = "dayBg_$day"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "dayText_$day"
                )

                Surface(
                    onClick = { onSelectDay(day) },
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    tonalElevation = if (isSelected) 3.dp else 0.dp,
                    border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) else null
                ) {
                    Text(
                        text = strings.dayN(day),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
