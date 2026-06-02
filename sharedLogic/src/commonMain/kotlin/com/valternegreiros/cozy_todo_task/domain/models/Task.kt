package com.valternegreiros.cozy_todo_task.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val categoryId: String? = null,
    val notes: String? = null,
    val checklist: List<ChecklistItem> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class ChecklistItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)
