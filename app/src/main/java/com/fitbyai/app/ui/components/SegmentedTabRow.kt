package com.fitbyai.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitbyai.app.i18n.LocalAppStrings

@Composable
fun M3SegmentedTabRow(
    selectedTab: String,
    queueCount: Int,
    doneCount: Int,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = modifier
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
                title = strings.inQueue(queueCount),
                selected = selectedTab == "queue",
                modifier = Modifier.weight(1f)
            ) {
                onTabSelected("queue")
            }

            SegmentedButtonOption(
                title = strings.completed(doneCount),
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
