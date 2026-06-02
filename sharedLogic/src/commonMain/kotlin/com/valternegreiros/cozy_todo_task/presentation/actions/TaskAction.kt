package com.valternegreiros.cozy_todo_task.presentation.actions

import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority

sealed interface TaskAction {
    data object AddClicked : TaskAction
    data class EditClicked(val taskId: String) : TaskAction
    data class DeleteClicked(val taskId: String) : TaskAction
    data class ToggleClicked(val taskId: String, val isCompleted: Boolean) : TaskAction
    data class FilterChanged(val filter: TaskFilter) : TaskAction
    data class PriorityFilterChanged(val priority: TaskPriority?) : TaskAction
    data class CategoryFilterChanged(val categoryId: String?) : TaskAction
    data object SaveDraftClicked : TaskAction
    data object CloseEditorClicked : TaskAction
}
