# Cozy Tasks Architecture

This document explains how Cozy Tasks is organized and how Android, Desktop, and iOS communicate with the shared Kotlin Multiplatform code.

The English architecture diagram is available at:

`docs/en_US/arch.png`

## Overview

The project uses Kotlin Multiplatform to keep business rules, state management, persistence, and task organization inside the `sharedLogic` module.

The visual layers are platform-specific:

- Android and Desktop use Compose Multiplatform through the `sharedUI` module.
- iOS uses native SwiftUI through the `iosApp` module.

The important point is that all platforms use the same shared `CozyTasksViewModel` written in Kotlin.

```text
Android/Desktop Compose UI
        |
        v
sharedLogic CozyTasksViewModel
        ^
        |
iOS SwiftUI -> CozyTasksObserver
```

## Modules

### `sharedLogic`

Path:

`sharedLogic/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task`

This module contains the shared app architecture:

- `domain`: models, repository interfaces, and use cases.
- `data`: repository implementations and local persistence.
- `presentation`: shared ViewModel, UI state, and actions.
- `core`: utilities, design tokens, clock, dispatchers, i18n, and result wrappers.
- `notifications`: prepared `expect/actual` layer for future local reminders.

### `sharedUI`

Path:

`sharedUI/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task`

This module contains the Compose UI used by Android and Desktop.

Main files:

- `App.kt`: Compose entry point.
- `ui/screens/CozyTasksScreen.kt`: main responsive screen.
- `ui/components/*`: cards, buttons, chips, task cards, dashboard, settings, navigation, and decorative elements.
- `ui/theme/CozyPalette.kt`: cozy color palette used by Compose.
- `ui/util/CozyFormatters.kt`: visual formatters used by Compose.

### `iosApp`

Path:

`iosApp/iosApp`

This module contains the native SwiftUI app.

Main files:

- `ContentView.swift`: main SwiftUI screen composition.
- `ViewModels/CozyTasksObserver.swift`: bridge between SwiftUI and the Kotlin ViewModel.
- `Views/*`: SwiftUI screen sections.
- `Components/*`: reusable SwiftUI components.
- `Theme/CozyColor.swift`: iOS color palette.
- `Support/TaskFormatters.swift`: formatters used by the iOS UI.

## `sharedLogic` Layers

### Domain

Main files:

- `domain/models/Task.kt`
- `domain/models/Category.kt`
- `domain/models/TaskPriority.kt`
- `domain/models/TaskStatus.kt`
- `domain/models/TaskFilter.kt`
- `domain/repository/TaskRepository.kt`
- `domain/usecases/TaskUseCases.kt`

The domain layer represents the core app rules.

It defines what a task is, what a category is, which priorities and filters exist, and which operations are available. The UI should not know persistence details and should not apply business rules by itself.

Example:

`TaskRepository.kt` defines the shared repository contract:

```kotlin
interface TaskRepository {
    val tasks: StateFlow<List<Task>>
    val categories: StateFlow<List<Category>>

    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun toggleTask(taskId: String, isCompleted: Boolean)
    suspend fun upsertCategory(category: Category)
}
```

`TaskUseCases.kt` contains rules such as filtering by today, upcoming, completed, overdue, priority, and category.

### Data

Main files:

- `data/repository/PersistedTaskRepository.kt`
- `data/local/TaskLocalStorage.kt`
- `data/local/TaskStoreSnapshot.kt`
- `androidMain/.../TaskLocalStorage.android.kt`
- `jvmMain/.../TaskLocalStorage.jvm.kt`
- `iosMain/.../TaskLocalStorage.ios.kt`

The `data` layer implements local persistence.

`PersistedTaskRepository` implements the `TaskRepository` interface. It keeps `StateFlow` instances for tasks and categories, applies changes, and saves a JSON snapshot using Kotlinx Serialization.

The storage mechanism itself is multiplatform:

```kotlin
expect class TaskLocalStorage() {
    suspend fun readText(): String?
    suspend fun writeText(content: String)
}
```

Each platform provides its own `actual` implementation:

- Android: writes JSON into `Context.filesDir`.
- Desktop: writes JSON into `~/.cozy-tasks/cozy_tasks_store.json`.
- iOS: writes JSON into `NSUserDefaults`.

Because of this, the repository does not need to know whether it is running on Android, Desktop, or iOS.

### Presentation

Main files:

- `presentation/viewmodels/CozyTasksViewModel.kt`
- `presentation/viewmodels/DisposableHandle.kt`
- `presentation/state/TaskUiState.kt`
- `presentation/state/TaskDraft.kt`
- `presentation/state/DashboardSummary.kt`
- `presentation/state/SettingsState.kt`
- `presentation/actions/TaskAction.kt`

This is the layer that communicates with the UIs.

`CozyTasksViewModel` exposes:

- `uiState: StateFlow<TaskUiState>`
- screen action methods such as `openNewTask`, `saveDraft`, `setFilter`, `setCompleted`, and `removeTask`.
- `observeState`, used by SwiftUI to observe state changes.
- `currentState`, used by SwiftUI to get the initial state.

`TaskUiState` contains everything the screen needs to render:

- greeting
- full task list
- filtered task list
- today's tasks
- categories
- dashboard summary
- selected filter
- selected task
- editor draft
- settings
- editor/modal state

## Android and Desktop Flow

Android and Desktop use the same Compose UI from `sharedUI`.

### Android

Entry file:

`androidApp/src/main/kotlin/com/valternegreiros/cozy_todo_task/MainActivity.kt`

Flow:

```text
MainActivity
  -> App()
  -> CozyTasksScreen()
  -> CozyTasksViewModel
  -> sharedLogic
```

`MainActivity` initializes Android storage:

```kotlin
AndroidCozyStorage.initialize(this)
```

Then it renders:

```kotlin
setContent {
    App()
}
```

### Desktop

Entry file:

`desktopApp/src/main/kotlin/com/valternegreiros/cozy_todo_task/main.kt`

Flow:

```text
main()
  -> Window
  -> App()
  -> CozyTasksScreen()
  -> CozyTasksViewModel
  -> sharedLogic
```

### Compose Collecting State

File:

`sharedUI/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task/App.kt`

Compose creates the shared ViewModel and observes the `StateFlow`:

```kotlin
fun App(viewModel: CozyTasksViewModel = remember { CozyTasksViewModel() }) {
    val state by viewModel.uiState.collectAsState()
    CozyTasksScreen(state, viewModel)
}
```

When the user interacts with the UI, Compose calls methods on the shared ViewModel:

```kotlin
viewModel.setFilter(TaskFilter.TODAY)
viewModel.openNewTask()
viewModel.saveDraft()
viewModel.setCompleted(task.id, true)
```

When the ViewModel updates the `StateFlow`, Compose receives the new state and recomposes the screen automatically.

## iOS Flow with SwiftUI

iOS does not use Compose. It uses native SwiftUI.

Even so, business rules still live in Kotlin inside `sharedLogic`.

### How Swift Accesses Kotlin

Kotlin Multiplatform generates an iOS framework named `SharedLogic`.

That is why Swift files can import:

```swift
import SharedLogic
```

Public Kotlin classes become available to Swift, including:

- `CozyTasksViewModel`
- `TaskUiState`
- `TaskFilter`
- `TaskPriority`
- `Task`
- `Category`
- `DisposableHandle`

The framework name comes from `sharedLogic/build.gradle.kts`:

```kotlin
iosTarget.binaries.framework {
    baseName = "SharedLogic"
    isStatic = true
}
```

The Xcode project runs the Gradle task that embeds the framework through a build phase:

```sh
./gradlew :sharedLogic:embedAndSignAppleFrameworkForXcode
```

### Main iOS Bridge File

File:

`iosApp/iosApp/ViewModels/CozyTasksObserver.swift`

This file is the bridge between SwiftUI and Kotlin.

It creates the Kotlin ViewModel:

```swift
let sharedViewModel = CozyTasksViewModel()
viewModel = sharedViewModel
```

It gets the initial state:

```swift
state = sharedViewModel.currentState()
```

It observes changes from the Kotlin `StateFlow`:

```swift
handle = viewModel.observeState { [weak self] next in
    DispatchQueue.main.async {
        self?.state = next
    }
}
```

The state is published to SwiftUI:

```swift
@Published var state: TaskUiState
```

SwiftUI updates the interface whenever this published state changes.

### Why `observeState` Exists

`StateFlow` is a Kotlin API. Swift does not collect `StateFlow` as directly as Compose does.

For that reason, Kotlin exposes this method in `CozyTasksViewModel.kt`:

```kotlin
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
```

This method adapts `StateFlow` into a callback that Swift can use.

Flow:

```text
Kotlin StateFlow<TaskUiState>
  -> observeState(callback)
  -> Swift closure receives TaskUiState
  -> DispatchQueue.main.async
  -> @Published state
  -> SwiftUI redraws
```

### How SwiftUI Sends Actions to Kotlin

`CozyTasksObserver` exposes small methods that call the Kotlin ViewModel:

```swift
func openNewTask() { viewModel.openNewTask() }
func saveDraft() { viewModel.saveDraft() }
func setFilter(_ filter: TaskFilter) { viewModel.setFilter(filter: filter) }
func setCompleted(_ taskId: String, _ completed: Bool) {
    viewModel.setCompleted(taskId: taskId, isCompleted: completed)
}
```

SwiftUI calls those methods.

Example in a SwiftUI screen:

```swift
FloatingAddButton(action: observer.openNewTask)
```

Or:

```swift
CozyChip(label: "Today", selected: state.selectedFilter == .today) {
    observer.setFilter(.today)
}
```

This means SwiftUI only sends user intent. It does not decide how to filter, save, organize, or persist tasks.

## iOS Files That Communicate with the Shared Code

### `ContentView.swift`

File:

`iosApp/iosApp/ContentView.swift`

This file composes the main SwiftUI screen.

It owns:

```swift
@StateObject private var observer = CozyTasksObserver()
```

It passes `observer.state` to child views:

```swift
DashboardHeaderView(state: observer.state)
TaskListView(state: observer.state, observer: observer)
SettingsPanelView(state: observer.state, observer: observer)
```

It also opens the editor when the Kotlin state says it should be open:

```swift
.sheet(isPresented: Binding(
    get: { observer.state.isEditorOpen },
    set: { if !$0 { observer.closeEditor() } }
)) {
    TaskEditorView(state: observer.state, observer: observer)
}
```

### `CozyTasksObserver.swift`

File:

`iosApp/iosApp/ViewModels/CozyTasksObserver.swift`

This is the main communication file with Kotlin.

It:

- imports `SharedLogic`.
- creates `CozyTasksViewModel`.
- observes Kotlin state.
- publishes state to SwiftUI.
- forwards UI actions to the Kotlin ViewModel.
- cancels observation in `deinit`.

### `DashboardSections.swift`

File:

`iosApp/iosApp/Views/DashboardSections.swift`

Reads `TaskUiState` coming from Kotlin:

```swift
let state: TaskUiState
```

Uses fields such as:

- `state.greeting`
- `state.headline`
- `state.summary.pending`
- `state.selectedFilter`

Sends filters to Kotlin through the observer:

```swift
observer.setFilter(.today)
```

### `TaskListView.swift`

File:

`iosApp/iosApp/Views/TaskListView.swift`

Reads the filtered task list from Kotlin:

```swift
let tasks = state.visibleTasks as? [SharedLogic.Task] ?? []
```

Renders each task and sends actions:

```swift
observer.editTask(task.id)
observer.setCompleted(task.id, !task.isCompleted)
```

### `TaskEditorView.swift`

File:

`iosApp/iosApp/Views/TaskEditorView.swift`

Reads the draft from Kotlin:

```swift
let draft = state.draft
```

Updates fields by calling observer methods, which forward the change to Kotlin:

```swift
CozyTextField(label: "Title", value: draft.title, onChange: observer.updateTitle)
CozyTextField(label: "Description", value: draft.description, onChange: observer.updateDescription)
```

Saves or deletes by calling:

```swift
observer.saveDraft()
observer.deleteTask(task.id)
```

### `SettingsPanelView.swift`

File:

`iosApp/iosApp/Views/SettingsPanelView.swift`

Reads settings from the Kotlin state:

```swift
state.settings.notificationsEnabled
state.settings.completionSoundEnabled
```

And sends changes:

```swift
observer.toggleNotifications
observer.toggleSound
```

## Complete iOS Action Flow

Example: the user marks a task as completed.

```text
1. User taps the checkbox in TaskListView.swift
2. SwiftUI calls observer.setCompleted(task.id, true)
3. CozyTasksObserver calls viewModel.setCompleted(...) in Kotlin
4. CozyTasksViewModel calls ToggleTaskUseCase
5. ToggleTaskUseCase calls TaskRepository.toggleTask
6. PersistedTaskRepository updates the in-memory list
7. PersistedTaskRepository writes the JSON snapshot to TaskLocalStorage
8. TaskLocalStorage.ios.kt saves it to NSUserDefaults
9. StateFlow<TaskUiState> emits a new state
10. observeState sends the new TaskUiState to Swift
11. CozyTasksObserver updates @Published state on the main thread
12. SwiftUI redraws the task list
```

## Complete Android/Desktop Action Flow

Example: the user changes the filter to "Today".

```text
1. User clicks the "Today" chip in Compose
2. FilterBar calls viewModel.setFilter(TaskFilter.TODAY)
3. CozyTasksViewModel updates selectedFilter
4. FilterTasksUseCase calculates visibleTasks
5. uiState emits a new TaskUiState
6. collectAsState receives the new state
7. Compose recomposes CozyTasksScreen
```

## Why the UI Has No Business Rules

The UIs do not filter tasks by themselves, do not calculate summaries, do not decide task status, and do not persist data directly.

They only:

- receive `TaskUiState`.
- render state on screen.
- send user actions to the ViewModel.

This keeps the central logic in one place:

`sharedLogic`

Benefits:

- Android, Desktop, and iOS behave consistently.
- Less duplication.
- Business rules are easier to test in Kotlin.
- SwiftUI stays simple and native.
- Compose stays focused on layout and interaction.

## Multiplatform Persistence

Persistence starts in the repository:

`PersistedTaskRepository.kt`

It transforms tasks and categories into:

```kotlin
TaskStoreSnapshot(
    tasks = ...,
    categories = ...
)
```

Then it serializes the snapshot to JSON and calls:

```kotlin
storage.writeText(json)
```

Each platform decides where to store the data:

### Android

File:

`sharedLogic/src/androidMain/kotlin/.../TaskLocalStorage.android.kt`

Uses `Context.filesDir`.

### Desktop

File:

`sharedLogic/src/jvmMain/kotlin/.../TaskLocalStorage.jvm.kt`

Uses:

`~/.cozy-tasks/cozy_tasks_store.json`

### iOS

File:

`sharedLogic/src/iosMain/kotlin/.../TaskLocalStorage.ios.kt`

Uses `NSUserDefaults`.

## Notifications

The notification architecture is prepared, but real reminders are not implemented yet.

Common file:

`sharedLogic/src/commonMain/kotlin/.../notifications/ReminderScheduler.kt`

Defines:

```kotlin
expect class ReminderScheduler() {
    suspend fun schedule(task: Task)
    suspend fun cancel(taskId: String)
}
```

Each platform has an `actual` implementation:

- Android: future use of `AlarmManager` or `WorkManager`.
- iOS: future use of `UNUserNotificationCenter`.
- Desktop: empty implementation for now.

## Contract Between iOS and Kotlin

The contract between SwiftUI and Kotlin is:

```text
SwiftUI renders TaskUiState
SwiftUI calls methods on CozyTasksObserver
CozyTasksObserver calls the Kotlin CozyTasksViewModel
CozyTasksViewModel changes sharedLogic state
StateFlow emits a new TaskUiState
observeState delivers the state to Swift
@Published state updates SwiftUI
```

Central files in this contract:

- Kotlin: `CozyTasksViewModel.kt`
- Kotlin: `TaskUiState.kt`
- Kotlin: `DisposableHandle.kt`
- Swift: `CozyTasksObserver.swift`
- Swift: `ContentView.swift`
- Swift: `TaskListView.swift`
- Swift: `TaskEditorView.swift`

This is what allows the project to use native SwiftUI on iOS without duplicating business logic outside `sharedLogic`.
