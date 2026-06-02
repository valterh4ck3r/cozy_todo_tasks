package com.valternegreiros.cozy_todo_task.presentation.state

import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStrings
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority

data class TaskUiState(
    val greeting: String = "Bom dia",
    val headline: String = "Vamos fazer um otimo dia!",
    val strings: CozyStringBundle = CozyStrings.bundle("pt_BR"),
    val tasks: List<Task> = emptyList(),
    val visibleTasks: List<Task> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val summary: DashboardSummary = DashboardSummary(),
    val selectedFilter: TaskFilter = TaskFilter.TODAY,
    val selectedPriority: TaskPriority? = null,
    val selectedCategoryId: String? = null,
    val selectedTask: Task? = null,
    val draft: TaskDraft = TaskDraft(),
    val settings: SettingsState = SettingsState(),
    val isEditorOpen: Boolean = false,
    val message: String? = null
)
