package com.valternegreiros.cozy_todo_task.domain.usecases

import com.valternegreiros.cozy_todo_task.data.repository.startOfDay
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): StateFlow<List<Task>> = repository.tasks
}

class CreateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.createTask(task)
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: String) = repository.deleteTask(taskId)
}

class ToggleTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: String, isCompleted: Boolean) = repository.toggleTask(taskId, isCompleted)
}

class FilterTasksUseCase {
    operator fun invoke(
        tasks: List<Task>,
        filter: TaskFilter,
        now: Long,
        selectedPriority: TaskPriority?,
        selectedCategoryId: String?
    ): List<Task> {
        val today = startOfDay(now)
        val tomorrow = today + DAY
        return when (filter) {
            TaskFilter.TODAY -> tasks.filter { it.dueDate != null && it.dueDate in today until tomorrow }
            TaskFilter.ALL -> tasks
            TaskFilter.UPCOMING -> tasks.filter { it.dueDate != null && it.dueDate >= tomorrow && !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            TaskFilter.OVERDUE -> tasks.filter { !it.isCompleted && it.dueDate != null && it.dueDate < today }
            TaskFilter.PRIORITY -> selectedPriority?.let { priority -> tasks.filter { it.priority == priority } } ?: tasks
            TaskFilter.CATEGORY -> selectedCategoryId?.let { categoryId -> tasks.filter { it.categoryId == categoryId } } ?: tasks
        }
    }

    private companion object {
        const val DAY = 24 * 60 * 60 * 1000L
    }
}
