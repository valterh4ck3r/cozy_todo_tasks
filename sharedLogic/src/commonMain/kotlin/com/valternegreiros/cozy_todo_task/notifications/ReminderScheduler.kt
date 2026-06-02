package com.valternegreiros.cozy_todo_task.notifications

import com.valternegreiros.cozy_todo_task.domain.models.Task

expect class ReminderScheduler() {
    suspend fun schedule(task: Task)
    suspend fun cancel(taskId: String)
}
