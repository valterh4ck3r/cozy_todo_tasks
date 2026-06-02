package com.valternegreiros.cozy_todo_task.presentation.state

import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority

data class TaskDraft(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val categoryId: String? = "work",
    val notes: String = "",
    val checklistText: String = ""
)
