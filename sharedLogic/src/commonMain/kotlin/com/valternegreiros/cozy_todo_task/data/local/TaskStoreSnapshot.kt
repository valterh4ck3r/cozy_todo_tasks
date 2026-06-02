package com.valternegreiros.cozy_todo_task.data.local

import com.valternegreiros.cozy_todo_task.domain.models.AppSettings
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.Task
import kotlinx.serialization.Serializable

@Serializable
data class TaskStoreSnapshot(
    val tasks: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val settings: AppSettings = AppSettings()
)
