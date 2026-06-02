package com.valternegreiros.cozy_todo_task.notifications

import com.valternegreiros.cozy_todo_task.domain.models.Task

actual class ReminderScheduler actual constructor() {
    actual suspend fun schedule(task: Task) = Unit
    actual suspend fun cancel(taskId: String) = Unit
}
