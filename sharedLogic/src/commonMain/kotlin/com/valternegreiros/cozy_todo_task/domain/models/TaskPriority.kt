package com.valternegreiros.cozy_todo_task.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
