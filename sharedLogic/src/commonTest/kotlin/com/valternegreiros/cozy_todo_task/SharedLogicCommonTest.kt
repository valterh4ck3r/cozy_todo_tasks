package com.valternegreiros.cozy_todo_task

import com.valternegreiros.cozy_todo_task.domain.models.AppSettings
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.repository.TaskRepository
import com.valternegreiros.cozy_todo_task.domain.usecases.FilterTasksUseCase
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicCommonTest {

    @Test
    fun todayFilterReturnsOnlyTasksDueToday() {
        val today = 1_728_000_000_000L
        val useCase = FilterTasksUseCase()
        val tasks = listOf(
            task("today", today + 10_000L),
            task("future", today + 24 * 60 * 60 * 1000L),
            task("no-date", null)
        )

        val result = useCase(
            tasks = tasks,
            filter = TaskFilter.TODAY,
            now = today,
            selectedPriority = null,
            selectedCategoryId = null
        )

        assertEquals(listOf("today"), result.map { it.id })
    }

    @Test
    fun changingLanguageUpdatesSharedUiStrings() {
        val viewModel = CozyTasksViewModel(repository = FakeTaskRepository())

        viewModel.setLanguage("en_US")

        assertEquals("Settings", viewModel.currentState().strings.settings)
        assertEquals("Tasks", viewModel.currentState().strings.tasks)
        assertEquals("en_US", viewModel.currentState().settings.language)
        viewModel.close()
    }

    private fun task(id: String, dueDate: Long?) = Task(
        id = id,
        title = id,
        priority = TaskPriority.MEDIUM,
        dueDate = dueDate,
        createdAt = 1L,
        updatedAt = 1L
    )

    private class FakeTaskRepository : TaskRepository {
        override val tasks: StateFlow<List<Task>> = MutableStateFlow(emptyList())
        override val categories: StateFlow<List<Category>> = MutableStateFlow(emptyList())
        private val settingsFlow = MutableStateFlow(AppSettings())
        override val settings: StateFlow<AppSettings> = settingsFlow

        override suspend fun createTask(task: Task) = Unit
        override suspend fun updateTask(task: Task) = Unit
        override suspend fun deleteTask(taskId: String) = Unit
        override suspend fun toggleTask(taskId: String, isCompleted: Boolean) = Unit
        override suspend fun upsertCategory(category: Category) = Unit
        override suspend fun updateSettings(settings: AppSettings) {
            settingsFlow.value = settings
        }
    }
}
