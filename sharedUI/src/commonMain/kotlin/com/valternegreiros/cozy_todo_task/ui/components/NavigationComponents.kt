@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.presentation.state.TaskUiState
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.theme.CozyCardCream
import com.valternegreiros.cozy_todo_task.ui.theme.CozyOrangeDark
import com.valternegreiros.cozy_todo_task.ui.theme.CozySoftBorder

@Composable
internal fun Sidebar(state: TaskUiState, viewModel: CozyTasksViewModel) {
    CozyCard(Modifier.width(220.dp).fillMaxHeight()) {
        Text("Cozy Tasks", fontSize = 28.sp, fontWeight = FontWeight.Black, color = CozyOrangeDark)
        Spacer(Modifier.height(16.dp))
        listOf(
            TaskFilter.TODAY to "Hoje",
            TaskFilter.ALL to "Todas",
            TaskFilter.UPCOMING to "Proximas",
            TaskFilter.COMPLETED to "Concluidas",
            TaskFilter.OVERDUE to "Atrasadas"
        ).forEach { (filter, label) ->
            NavPill(label, state.selectedFilter == filter) { viewModel.setFilter(filter) }
        }
        Spacer(Modifier.height(14.dp))
        Text("Categorias", fontWeight = FontWeight.Bold)
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
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip("Todas", state.selectedFilter == TaskFilter.ALL) { viewModel.setFilter(TaskFilter.ALL) }
        FilterChip("Hoje", state.selectedFilter == TaskFilter.TODAY) { viewModel.setFilter(TaskFilter.TODAY) }
        FilterChip("Proximas", state.selectedFilter == TaskFilter.UPCOMING) { viewModel.setFilter(TaskFilter.UPCOMING) }
        FilterChip("Concluidas", state.selectedFilter == TaskFilter.COMPLETED) { viewModel.setFilter(TaskFilter.COMPLETED) }
        FilterChip("Atrasadas", state.selectedFilter == TaskFilter.OVERDUE) { viewModel.setFilter(TaskFilter.OVERDUE) }
        PriorityChip(TaskPriority.HIGH, selected = state.selectedPriority == TaskPriority.HIGH) { viewModel.setPriorityFilter(TaskPriority.HIGH) }
        PriorityChip(TaskPriority.MEDIUM, selected = state.selectedPriority == TaskPriority.MEDIUM) { viewModel.setPriorityFilter(TaskPriority.MEDIUM) }
        PriorityChip(TaskPriority.LOW, selected = state.selectedPriority == TaskPriority.LOW) { viewModel.setPriorityFilter(TaskPriority.LOW) }
    }
}

@Composable
internal fun BottomTabs(selected: TaskFilter, viewModel: CozyTasksViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(CozyCardCream)
            .border(1.dp, CozySoftBorder.copy(alpha = 0.6f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        NavPill("Hoje", selected == TaskFilter.TODAY, Modifier.weight(1f)) { viewModel.setFilter(TaskFilter.TODAY) }
        NavPill("Todas", selected == TaskFilter.ALL, Modifier.weight(1f)) { viewModel.setFilter(TaskFilter.ALL) }
        NavPill("Prox.", selected == TaskFilter.UPCOMING, Modifier.weight(1f)) { viewModel.setFilter(TaskFilter.UPCOMING) }
        NavPill("Mais", selected == TaskFilter.COMPLETED, Modifier.weight(1f)) { viewModel.setFilter(TaskFilter.COMPLETED) }
    }
}
