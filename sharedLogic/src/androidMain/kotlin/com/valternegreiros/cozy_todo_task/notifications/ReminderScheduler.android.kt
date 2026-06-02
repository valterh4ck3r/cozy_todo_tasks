package com.valternegreiros.cozy_todo_task.notifications

import com.valternegreiros.cozy_todo_task.domain.models.Task

actual class ReminderScheduler actual constructor() {
    actual suspend fun schedule(task: Task) {
        // Hook for AlarmManager/WorkManager local reminders.
    }

    actual suspend fun cancel(taskId: String) {
        // Hook for cancelling scheduled Android reminders.
    }
}
