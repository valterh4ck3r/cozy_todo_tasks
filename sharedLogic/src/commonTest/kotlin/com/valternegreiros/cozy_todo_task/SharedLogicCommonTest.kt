package com.valternegreiros.cozy_todo_task

import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.domain.usecases.FilterTasksUseCase
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

    private fun task(id: String, dueDate: Long?) = Task(
        id = id,
        title = id,
        priority = TaskPriority.MEDIUM,
        dueDate = dueDate,
        createdAt = 1L,
        updatedAt = 1L
    )
}
