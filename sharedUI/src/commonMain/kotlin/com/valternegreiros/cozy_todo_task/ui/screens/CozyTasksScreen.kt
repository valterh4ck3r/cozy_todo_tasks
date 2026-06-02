package com.valternegreiros.cozy_todo_task.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valternegreiros.cozy_todo_task.presentation.state.TaskUiState
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.components.DashboardHeader
import com.valternegreiros.cozy_todo_task.ui.components.DashboardSummaryCard
import com.valternegreiros.cozy_todo_task.ui.components.DecorativeLeaves
import com.valternegreiros.cozy_todo_task.ui.components.EmptyState
import com.valternegreiros.cozy_todo_task.ui.components.FilterBar
import com.valternegreiros.cozy_todo_task.ui.components.FloatingAddButton
import com.valternegreiros.cozy_todo_task.ui.components.SectionTitle
import com.valternegreiros.cozy_todo_task.ui.components.SettingsPanel
import com.valternegreiros.cozy_todo_task.ui.components.Sidebar
import com.valternegreiros.cozy_todo_task.ui.components.TaskCard
import com.valternegreiros.cozy_todo_task.ui.components.TaskEditor
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme

@Composable
internal fun CozyTasksScreen(state: TaskUiState, viewModel: CozyTasksViewModel) {
    val colors = CozyTheme.colors
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        DecorativeLeaves()
        val isDesktop = maxWidth >= 840.dp
        if (isDesktop) {
            DesktopShell(state, viewModel)
        } else {
            MobileShell(state, viewModel)
        }

        AnimatedVisibility(
            visible = state.isEditorOpen,
            modifier = Modifier.align(Alignment.Center)
        ) {
            TaskEditor(state.draft, state.categories, state.selectedTask, state.strings, viewModel)
        }

        FloatingAddButton(
            onClick = viewModel::openNewTask,
            modifier = if (isDesktop) {
                Modifier.align(Alignment.BottomEnd).padding(24.dp)
            } else {
                Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 18.dp, bottom = 18.dp)
            }
        )
    }
}

@Composable
private fun DesktopShell(state: TaskUiState, viewModel: CozyTasksViewModel) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Sidebar(state, viewModel)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { DashboardHeader(state) }
            item { DashboardSummaryCard(state.summary, state.strings) }
            item { FilterBar(state, viewModel) }
            item { SectionTitle(state.strings.today) }
            if (state.visibleTasks.isEmpty()) {
                item { EmptyState(state.strings.desktopEmptyTitle, state.strings.desktopEmptyBody) }
            } else {
                items(state.visibleTasks, key = { it.id }) { task ->
                    TaskCard(task, state.categories, state.strings, viewModel)
                }
            }
        }
        SettingsPanel(state.settings, state.strings, viewModel, Modifier.width(300.dp))
    }
}

@Composable
private fun MobileShell(state: TaskUiState, viewModel: CozyTasksViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item { DashboardHeader(state) }
        item { DashboardSummaryCard(state.summary, state.strings) }
        item { FilterBar(state, viewModel) }
        item { SectionTitle(state.strings.tasks) }
        if (state.visibleTasks.isEmpty()) {
            item { EmptyState(state.strings.emptyTitle, state.strings.emptyBody) }
        } else {
            items(state.visibleTasks, key = { it.id }) { task ->
                TaskCard(task, state.categories, state.strings, viewModel)
            }
        }
        item { SettingsPanel(state.settings, state.strings, viewModel, Modifier.fillMaxWidth()) }
        item { Spacer(Modifier.height(96.dp)) }
    }
}
