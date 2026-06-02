package com.valternegreiros.cozy_todo_task.presentation.viewmodels

import com.valternegreiros.cozy_todo_task.core.dispatchers.AppDispatchers
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStrings
import com.valternegreiros.cozy_todo_task.core.time.Clock
import com.valternegreiros.cozy_todo_task.core.time.SystemClock
import com.valternegreiros.cozy_todo_task.data.repository.PersistedTaskRepository
import com.valternegreiros.cozy_todo_task.data.repository.startOfDay
import com.valternegreiros.cozy_todo_task.domain.models.ChecklistItem
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskFilter
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.domain.repository.TaskRepository
import com.valternegreiros.cozy_todo_task.domain.usecases.CreateTaskUseCase
import com.valternegreiros.cozy_todo_task.domain.usecases.DeleteTaskUseCase
import com.valternegreiros.cozy_todo_task.domain.usecases.FilterTasksUseCase
import com.valternegreiros.cozy_todo_task.domain.usecases.ToggleTaskUseCase
import com.valternegreiros.cozy_todo_task.domain.usecases.UpdateTaskUseCase
import com.valternegreiros.cozy_todo_task.presentation.actions.TaskAction
import com.valternegreiros.cozy_todo_task.presentation.state.DashboardSummary
import com.valternegreiros.cozy_todo_task.presentation.state.SettingsState
import com.valternegreiros.cozy_todo_task.presentation.state.TaskDraft
import com.valternegreiros.cozy_todo_task.presentation.state.TaskUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CozyTasksViewModel(
    private val repository: TaskRepository = PersistedTaskRepository(),
    private val clock: Clock = SystemClock()
) {
    private val scope = CoroutineScope(SupervisorJob() + AppDispatchers.background)
    private val createTask = CreateTaskUseCase(repository)
    private val updateTask = UpdateTaskUseCase(repository)
    private val deleteTask = DeleteTaskUseCase(repository)
    private val toggleTask = ToggleTaskUseCase(repository)
    private val filterTasks = FilterTasksUseCase()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(repository.tasks, repository.categories) { tasks, categories ->
                buildState(tasks, categories, _uiState.value)
            }.collectLatest { nextState ->
                _uiState.value = nextState
            }
        }
    }

    fun currentState(): TaskUiState = uiState.value

    fun dispatch(action: TaskAction) {
        when (action) {
            TaskAction.AddClicked -> openNewTask()
            is TaskAction.EditClicked -> editTask(action.taskId)
            is TaskAction.DeleteClicked -> removeTask(action.taskId)
            is TaskAction.ToggleClicked -> setCompleted(action.taskId, action.isCompleted)
            is TaskAction.FilterChanged -> setFilter(action.filter)
            is TaskAction.PriorityFilterChanged -> setPriorityFilter(action.priority)
            is TaskAction.CategoryFilterChanged -> setCategoryFilter(action.categoryId)
            TaskAction.SaveDraftClicked -> saveDraft()
            TaskAction.CloseEditorClicked -> closeEditor()
        }
    }

    fun openNewTask() {
        _uiState.value = _uiState.value.copy(draft = TaskDraft(), selectedTask = null, isEditorOpen = true)
    }

    fun editTask(taskId: String) {
        val task = _uiState.value.tasks.firstOrNull { it.id == taskId } ?: return
        _uiState.value = _uiState.value.copy(
            selectedTask = task,
            isEditorOpen = true,
            draft = TaskDraft(
                id = task.id,
                title = task.title,
                description = task.description.orEmpty(),
                priority = task.priority,
                dueDate = task.dueDate,
                categoryId = task.categoryId,
                notes = task.notes.orEmpty(),
                checklistText = task.checklist.joinToString("\n") { it.title }
            )
        )
    }

    fun setFilter(filter: TaskFilter) {
        val current = _uiState.value
        _uiState.value = buildState(current.tasks, current.categories, current.copy(selectedFilter = filter))
    }

    fun setPriorityFilter(priority: TaskPriority?) {
        val current = _uiState.value
        _uiState.value = buildState(
            current.tasks,
            current.categories,
            current.copy(selectedFilter = TaskFilter.PRIORITY, selectedPriority = priority)
        )
    }

    fun setCategoryFilter(categoryId: String?) {
        val current = _uiState.value
        _uiState.value = buildState(
            current.tasks,
            current.categories,
            current.copy(selectedFilter = TaskFilter.CATEGORY, selectedCategoryId = categoryId)
        )
    }

    fun updateDraftTitle(value: String) = updateDraft { copy(title = value) }
    fun updateDraftDescription(value: String) = updateDraft { copy(description = value) }
    fun updateDraftPriority(value: TaskPriority) = updateDraft { copy(priority = value) }
    fun updateDraftDueDate(value: Long?) = updateDraft { copy(dueDate = value) }
    fun updateDraftCategory(value: String?) = updateDraft { copy(categoryId = value) }
    fun updateDraftNotes(value: String) = updateDraft { copy(notes = value) }
    fun updateDraftChecklist(value: String) = updateDraft { copy(checklistText = value) }

    fun saveDraft() {
        val draft = _uiState.value.draft
        val title = draft.title.trim()
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(message = _uiState.value.strings.taskTitleRequired)
            return
        }

        scope.launch {
            val now = clock.now()
            val checklist = draft.checklistText.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .mapIndexed { index, item -> ChecklistItem("${draft.id ?: now}-$index", item) }

            if (draft.id == null) {
                createTask(
                    Task(
                        id = newPresentationId(now),
                        title = title,
                        description = draft.description.takeIf { it.isNotBlank() },
                        priority = draft.priority,
                        dueDate = draft.dueDate,
                        categoryId = draft.categoryId,
                        notes = draft.notes.takeIf { it.isNotBlank() },
                        checklist = checklist,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } else {
                val original = _uiState.value.tasks.firstOrNull { it.id == draft.id } ?: return@launch
                updateTask(
                    original.copy(
                        title = title,
                        description = draft.description.takeIf { it.isNotBlank() },
                        priority = draft.priority,
                        dueDate = draft.dueDate,
                        categoryId = draft.categoryId,
                        notes = draft.notes.takeIf { it.isNotBlank() },
                        checklist = checklist,
                        updatedAt = now
                    )
                )
            }
            closeEditor()
        }
    }

    fun removeTask(taskId: String) {
        scope.launch {
            deleteTask(taskId)
            if (_uiState.value.selectedTask?.id == taskId) closeEditor()
        }
    }

    fun setCompleted(taskId: String, isCompleted: Boolean) {
        scope.launch {
            toggleTask(taskId, isCompleted)
        }
    }

    fun toggleDarkTheme(value: Boolean) = updateSettings { copy(darkTheme = value) }
    fun toggleNotifications(value: Boolean) = updateSettings { copy(notificationsEnabled = value) }
    fun toggleCompletionSound(value: Boolean) = updateSettings { copy(completionSoundEnabled = value) }
    fun setLanguage(value: String) {
        val current = _uiState.value
        val next = current.copy(settings = current.settings.copy(language = value))
        _uiState.value = buildState(current.tasks, current.categories, next)
    }

    fun closeEditor() {
        _uiState.value = _uiState.value.copy(isEditorOpen = false, selectedTask = null, draft = TaskDraft())
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun observeState(onChange: (TaskUiState) -> Unit): DisposableHandle {
        val job: Job = scope.launch {
            uiState.collectLatest { onChange(it) }
        }
        return object : DisposableHandle {
            override fun dispose() {
                job.cancel()
            }
        }
    }

    fun close() {
        scope.cancel()
    }

    private fun updateDraft(change: TaskDraft.() -> TaskDraft) {
        _uiState.value = _uiState.value.copy(draft = _uiState.value.draft.change())
    }

    private fun updateSettings(change: SettingsState.() -> SettingsState) {
        _uiState.value = _uiState.value.copy(settings = _uiState.value.settings.change())
    }

    private fun buildState(
        tasks: List<Task>,
        categories: List<com.valternegreiros.cozy_todo_task.domain.models.Category>,
        current: TaskUiState
    ): TaskUiState {
        val now = clock.now()
        val today = startOfDay(now)
        val tomorrow = today + DAY
        val todayTasks = tasks.filter { it.dueDate != null && it.dueDate in today until tomorrow }
        val strings = CozyStrings.bundle(current.settings.language)
        val summary = DashboardSummary(
            pending = tasks.count { !it.isCompleted },
            completed = tasks.count { it.isCompleted },
            overdue = tasks.count { !it.isCompleted && it.dueDate != null && it.dueDate < today },
            today = todayTasks.size
        )
        return current.copy(
            greeting = greetingFor(now, strings),
            headline = strings.headline,
            strings = strings,
            tasks = tasks,
            visibleTasks = filterTasks(tasks, current.selectedFilter, now, current.selectedPriority, current.selectedCategoryId),
            todayTasks = todayTasks,
            categories = categories,
            summary = summary
        )
    }

    private fun greetingFor(now: Long, strings: CozyStringBundle): String {
        val hour = ((now % DAY) / HOUR).toInt()
        return when (hour) {
            in 5..11 -> strings.morningGreeting
            in 12..17 -> strings.afternoonGreeting
            else -> strings.eveningGreeting
        }
    }

    private fun newPresentationId(now: Long): String = "$now-${_uiState.value.tasks.size + 1}"

    private companion object {
        const val HOUR = 60 * 60 * 1000L
        const val DAY = 24 * HOUR
    }
}
