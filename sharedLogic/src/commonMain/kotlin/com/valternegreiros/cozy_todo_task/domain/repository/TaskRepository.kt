package com.valternegreiros.cozy_todo_task.domain.repository

import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.Task
import kotlinx.coroutines.flow.StateFlow

interface TaskRepository {
    val tasks: StateFlow<List<Task>>
    val categories: StateFlow<List<Category>>

    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun toggleTask(taskId: String, isCompleted: Boolean)
    suspend fun upsertCategory(category: Category)
}
