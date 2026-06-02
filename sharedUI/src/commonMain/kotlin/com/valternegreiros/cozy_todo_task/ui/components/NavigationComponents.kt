@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.presentation.state.TaskUiState
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme

@Composable
internal fun Sidebar(state: TaskUiState, viewModel: CozyTasksViewModel) {
    val colors = CozyTheme.colors
    CozyCard(Modifier.width(220.dp).fillMaxHeight()) {
        Text(state.strings.appName, fontSize = 28.sp, fontWeight = FontWeight.Black, color = colors.orangeDark)
        Spacer(Modifier.height(16.dp))
        listOf(
            TaskFilter.TODAY to state.strings.today,
            TaskFilter.ALL to state.strings.all,
            TaskFilter.UPCOMING to state.strings.upcoming,
            TaskFilter.COMPLETED to state.strings.completed,
            TaskFilter.OVERDUE to state.strings.overdue
        ).forEach { (filter, label) ->
            NavPill(label, state.selectedFilter == filter) { viewModel.setFilter(filter) }
        }
        Spacer(Modifier.height(14.dp))
        Text(state.strings.categories, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        state.categories.forEach { category ->
            CategoryChip(category, selected = state.selectedCategoryId == category.id) {
                viewModel.setCategoryFilter(category.id)
            }
        }
    }
}

@Composable
internal fun FilterBar(state: TaskUiState, viewModel: CozyTasksViewModel) {
    val strings = state.strings
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(strings.all, state.selectedFilter == TaskFilter.ALL) { viewModel.setFilter(TaskFilter.ALL) }
        FilterChip(strings.today, state.selectedFilter == TaskFilter.TODAY) { viewModel.setFilter(TaskFilter.TODAY) }
        FilterChip(strings.upcoming, state.selectedFilter == TaskFilter.UPCOMING) { viewModel.setFilter(TaskFilter.UPCOMING) }
        FilterChip(strings.completed, state.selectedFilter == TaskFilter.COMPLETED) { viewModel.setFilter(TaskFilter.COMPLETED) }
        FilterChip(strings.overdue, state.selectedFilter == TaskFilter.OVERDUE) { viewModel.setFilter(TaskFilter.OVERDUE) }
        PriorityChip(TaskPriority.HIGH, selected = state.selectedPriority == TaskPriority.HIGH, strings = strings) { viewModel.setPriorityFilter(TaskPriority.HIGH) }
        PriorityChip(TaskPriority.MEDIUM, selected = state.selectedPriority == TaskPriority.MEDIUM, strings = strings) { viewModel.setPriorityFilter(TaskPriority.MEDIUM) }
        PriorityChip(TaskPriority.LOW, selected = state.selectedPriority == TaskPriority.LOW, strings = strings) { viewModel.setPriorityFilter(TaskPriority.LOW) }
    }
}
