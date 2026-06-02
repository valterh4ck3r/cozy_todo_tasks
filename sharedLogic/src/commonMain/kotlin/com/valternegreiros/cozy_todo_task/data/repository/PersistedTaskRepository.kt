package com.valternegreiros.cozy_todo_task.data.repository

import com.valternegreiros.cozy_todo_task.core.time.Clock
import com.valternegreiros.cozy_todo_task.core.time.SystemClock
import com.valternegreiros.cozy_todo_task.data.local.TaskLocalStorage
import com.valternegreiros.cozy_todo_task.data.local.TaskStoreSnapshot
import com.valternegreiros.cozy_todo_task.domain.models.AppSettings
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlin.random.Random

class PersistedTaskRepository(
    private val storage: TaskLocalStorage = TaskLocalStorage(),
    private val clock: Clock = SystemClock(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
) : TaskRepository {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val mutex = Mutex()
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    private val _categories = MutableStateFlow(defaultCategories())
    private val _settings = MutableStateFlow(AppSettings())

    override val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    override val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        scope.launch {
            load()
        }
    }

    override suspend fun createTask(task: Task) = persist {
        _tasks.value = (_tasks.value + task).sortedForCozy()
    }

    override suspend fun updateTask(task: Task) = persist {
        _tasks.value = _tasks.value.map { current ->
            if (current.id == task.id) task.copy(updatedAt = clock.now()) else current
        }.sortedForCozy()
    }

    override suspend fun deleteTask(taskId: String) = persist {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
    }

    override suspend fun toggleTask(taskId: String, isCompleted: Boolean) = persist {
        val now = clock.now()
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = isCompleted, updatedAt = now) else task
        }.sortedForCozy()
    }

    override suspend fun upsertCategory(category: Category) = persist {
        val existing = _categories.value.any { it.id == category.id }
        _categories.value = if (existing) {
            _categories.value.map { if (it.id == category.id) category else it }
        } else {
            _categories.value + category
        }
    }

    override suspend fun updateSettings(settings: AppSettings) = persist {
        _settings.value = settings
    }

    private suspend fun load() {
        mutex.withLock {
            val content = storage.readText()
            if (content.isNullOrBlank()) {
                seed()
                writeSnapshot()
                return
            }

            runCatching { json.decodeFromString<TaskStoreSnapshot>(content) }
                .onSuccess { snapshot ->
                    _tasks.value = snapshot.tasks.sortedForCozy()
                    _categories.value = if (snapshot.categories.isEmpty()) defaultCategories() else snapshot.categories
                    _settings.value = snapshot.settings
                }
                .onFailure {
                    seed()
                    writeSnapshot()
                }
        }
    }

    private suspend fun persist(change: () -> Unit) {
        mutex.withLock {
            change()
            writeSnapshot()
        }
    }

    private suspend fun writeSnapshot() {
        storage.writeText(json.encodeToString(TaskStoreSnapshot(_tasks.value, _categories.value, _settings.value)))
    }

    private fun seed() {
        val now = clock.now()
        val todayMorning = startOfDay(now) + 10 * HOUR
        _categories.value = defaultCategories()
        _tasks.value = listOf(
            Task(
                id = newId(),
                title = "Reunião com o time",
                description = "Alinhar prioridades da semana.",
                priority = TaskPriority.HIGH,
                dueDate = todayMorning,
                categoryId = "work",
                createdAt = now,
                updatedAt = now
            ),
            Task(
                id = newId(),
                title = "Finalizar apresentação",
                priority = TaskPriority.MEDIUM,
                dueDate = startOfDay(now) + 14 * HOUR + 30 * MINUTE,
                categoryId = "projects",
                createdAt = now,
                updatedAt = now
            ),
            Task(
                id = newId(),
                title = "Responder e-mails",
                isCompleted = true,
                priority = TaskPriority.LOW,
                dueDate = startOfDay(now) + 9 * HOUR + 15 * MINUTE,
                categoryId = "work",
                createdAt = now,
                updatedAt = now
            )
        ).sortedForCozy()
    }

    private fun List<Task>.sortedForCozy(): List<Task> =
        sortedWith(compareBy<Task> { it.isCompleted }
            .thenBy { it.dueDate ?: Long.MAX_VALUE }
            .thenByDescending { it.priority.ordinal }
            .thenBy { it.title.lowercase() })
}

fun defaultCategories(): List<Category> = listOf(
    Category("work", "Trabalho", "F6A23A"),
    Category("personal", "Pessoal", "5E9D32"),
    Category("study", "Estudos", "7AA7C7"),
    Category("shopping", "Compras", "C96F22"),
    Category("projects", "Projetos", "E85D2A")
)

fun newId(): String = "${kotlin.time.Clock.System.now().toEpochMilliseconds()}-${Random.nextInt(1000, 9999)}"

private const val MINUTE = 60_000L
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR

fun startOfDay(millis: Long): Long = (millis / DAY) * DAY
