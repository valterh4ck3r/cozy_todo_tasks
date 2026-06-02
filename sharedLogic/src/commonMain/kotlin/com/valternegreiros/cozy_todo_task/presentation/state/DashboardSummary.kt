package com.valternegreiros.cozy_todo_task.presentation.state

data class DashboardSummary(
    val pending: Int = 0,
    val completed: Int = 0,
    val overdue: Int = 0,
    val today: Int = 0
)
