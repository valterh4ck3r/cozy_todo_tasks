package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.presentation.state.DashboardSummary
import com.valternegreiros.cozy_todo_task.presentation.state.TaskUiState
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme

@Composable
internal fun DashboardHeader(state: TaskUiState) {
    BoxWithConstraints {
        val compact = maxWidth < 430.dp
        CozyCard(Modifier.fillMaxWidth(), padding = if (compact) 14 else 16) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${state.greeting}!",
                        fontSize = if (compact) 16.sp else 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Text(
                        state.headline,
                        fontSize = if (compact) 26.sp else 30.sp,
                        lineHeight = if (compact) 29.sp else 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                PlantPot(Modifier.size(if (compact) 66.dp else 94.dp))
            }
        }
    }
}

@Composable
internal fun DashboardSummaryCard(summary: DashboardSummary, strings: CozyStringBundle) {
    val colors = CozyTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryTile(strings.pendingShort, summary.pending, colors.orange, Modifier.weight(1f))
        SummaryTile(strings.completedShort, summary.completed, colors.green, Modifier.weight(1f))
        SummaryTile(strings.overdueShort, summary.overdue, colors.red, Modifier.weight(1f))
        SummaryTile(strings.today, summary.today, colors.gold, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryTile(label: String, value: Int, accent: Color, modifier: Modifier) {
    CozyCard(modifier = modifier.height(92.dp), padding = 10) {
        Box(
            Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent),
            contentAlignment = Alignment.Center
        ) {
            Text(value.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
