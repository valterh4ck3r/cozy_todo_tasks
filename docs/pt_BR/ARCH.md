# Cozy Tasks Architecture

Este documento explica como o Cozy Tasks está organizado e como Android, Desktop e iOS se comunicam com o código compartilhado em Kotlin Multiplatform.

## Visão Geral

O projeto usa Kotlin Multiplatform para concentrar regras de negócio, estado, persistência e organização de tarefas no módulo `sharedLogic`.

As interfaces visuais ficam separadas por plataforma:

- Android e Desktop usam Compose Multiplatform no módulo `sharedUI`.
- iOS usa SwiftUI no módulo `iosApp`.

O ponto importante é que as três plataformas usam o mesmo `CozyTasksViewModel` compartilhado em Kotlin.

```text
Android/Desktop Compose UI
        |
        v
sharedLogic CozyTasksViewModel
        ^
        |
iOS SwiftUI -> CozyTasksObserver
```

## Módulos

### `sharedLogic`

Caminho:

`sharedLogic/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task`

Este módulo contém a arquitetura compartilhada do app:

- `domain`: modelos, interfaces de repositório e use cases.
- `data`: implementação de repositório e persistência local.
- `presentation`: ViewModel compartilhado, estado de UI e actions.
- `core`: utilitários, tokens, clock, dispatchers, i18n e wrappers.
- `notifications`: camada preparada com `expect/actual` para lembretes locais.

### `sharedUI`

Caminho:

`sharedUI/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task`

Contém a UI Compose usada por Android e Desktop.

Principais arquivos:

- `App.kt`: entry point Compose.
- `ui/screens/CozyTasksScreen.kt`: tela principal responsiva.
- `ui/components/*`: cards, botões, chips, task cards, dashboard, settings e navegação.
- `ui/theme/CozyPalette.kt`: cores cozy usadas pela UI.
- `ui/util/CozyFormatters.kt`: formatadores visuais.

### `iosApp`

Caminho:

`iosApp/iosApp`

Contém a UI nativa em SwiftUI.

Principais arquivos:

- `ContentView.swift`: composição principal da tela.
- `ViewModels/CozyTasksObserver.swift`: ponte entre SwiftUI e o ViewModel Kotlin.
- `Views/*`: partes da tela SwiftUI.
- `Components/*`: componentes SwiftUI reutilizáveis.
- `Theme/CozyColor.swift`: paleta visual iOS.
- `Support/TaskFormatters.swift`: formatadores usados pela UI iOS.

## Camadas do `sharedLogic`

### Domain

Arquivos principais:

- `domain/models/Task.kt`
- `domain/models/Category.kt`
- `domain/models/TaskPriority.kt`
- `domain/models/TaskStatus.kt`
- `domain/models/TaskFilter.kt`
- `domain/repository/TaskRepository.kt`
- `domain/usecases/TaskUseCases.kt`

Esta camada representa as regras centrais do app.

Ela define o que é uma tarefa, categoria, prioridade, filtro e quais operações existem. A UI não deve conhecer detalhes de persistência nem aplicar regras de negócio por conta própria.

Exemplo:

`TaskRepository.kt` define a interface:

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

Já `TaskUseCases.kt` contém regras como filtro por hoje, próximas, concluídas, atrasadas, prioridade e categoria.

### Data

Arquivos principais:

- `data/repository/PersistedTaskRepository.kt`
- `data/local/TaskLocalStorage.kt`
- `data/local/TaskStoreSnapshot.kt`
- `androidMain/.../TaskLocalStorage.android.kt`
- `jvmMain/.../TaskLocalStorage.jvm.kt`
- `iosMain/.../TaskLocalStorage.ios.kt`

A camada `data` implementa a persistência local.

`PersistedTaskRepository` implementa a interface `TaskRepository`. Ele mantém `StateFlow` de tarefas e categorias, aplica alterações e grava um snapshot JSON usando Kotlinx Serialization.

O armazenamento em si é multiplataforma:

```kotlin
expect class TaskLocalStorage() {
    suspend fun readText(): String?
    suspend fun writeText(content: String)
}
```

Cada plataforma fornece seu `actual`:

- Android: grava JSON em `Context.filesDir`.
- Desktop: grava JSON em `~/.cozy-tasks/cozy_tasks_store.json`.
- iOS: grava JSON em `NSUserDefaults`.

Assim, o repository não precisa saber se está rodando em Android, Desktop ou iOS.

### Presentation

Arquivos principais:

- `presentation/viewmodels/CozyTasksViewModel.kt`
- `presentation/viewmodels/DisposableHandle.kt`
- `presentation/state/TaskUiState.kt`
- `presentation/state/TaskDraft.kt`
- `presentation/state/DashboardSummary.kt`
- `presentation/state/SettingsState.kt`
- `presentation/actions/TaskAction.kt`

Esta é a camada que conversa com as UIs.

`CozyTasksViewModel` expõe:

- `uiState: StateFlow<TaskUiState>`
- métodos para ações da tela, como `openNewTask`, `saveDraft`, `setFilter`, `setCompleted`, `removeTask`.
- `observeState`, usado pelo SwiftUI para observar mudanças.
- `currentState`, usado pelo SwiftUI para pegar o estado inicial.

O `TaskUiState` contém tudo que a tela precisa renderizar:

- saudação
- lista completa de tarefas
- lista filtrada
- tarefas de hoje
- categorias
- resumo do dashboard
- filtro selecionado
- tarefa selecionada
- draft do editor
- configurações
- estado do modal/editor

## Fluxo no Android e Desktop

Android e Desktop usam a mesma UI Compose em `sharedUI`.

### Android

Arquivo de entrada:

`androidApp/src/main/kotlin/com/valternegreiros/cozy_todo_task/MainActivity.kt`

Fluxo:

```text
MainActivity
  -> App()
  -> CozyTasksScreen()
  -> CozyTasksViewModel
  -> sharedLogic
```

`MainActivity` inicializa o storage Android:

```kotlin
AndroidCozyStorage.initialize(this)
```

Depois renderiza:

```kotlin
setContent {
    App()
}
```

### Desktop

Arquivo de entrada:

`desktopApp/src/main/kotlin/com/valternegreiros/cozy_todo_task/main.kt`

Fluxo:

```text
main()
  -> Window
  -> App()
  -> CozyTasksScreen()
  -> CozyTasksViewModel
  -> sharedLogic
```

### Compose coletando estado

Arquivo:

`sharedUI/src/commonMain/kotlin/com/valternegreiros/cozy_todo_task/App.kt`

O Compose cria o ViewModel compartilhado e observa o `StateFlow`:

```kotlin
fun App(viewModel: CozyTasksViewModel = remember { CozyTasksViewModel() }) {
    val state by viewModel.uiState.collectAsState()
    CozyTasksScreen(state, viewModel)
}
```

Quando o usuário interage, a UI chama métodos do ViewModel:

```kotlin
viewModel.setFilter(TaskFilter.TODAY)
viewModel.openNewTask()
viewModel.saveDraft()
viewModel.setCompleted(task.id, true)
```

Quando o ViewModel atualiza o `StateFlow`, o Compose recompõe a tela automaticamente.

## Fluxo no iOS com SwiftUI

O iOS não usa Compose. Ele usa SwiftUI nativo.

Mesmo assim, a regra de negócio continua no Kotlin, dentro do `sharedLogic`.

### Como o Swift acessa o Kotlin

O Kotlin Multiplatform gera um framework iOS chamado `SharedLogic`.

Por isso, os arquivos Swift importam:

```swift
import SharedLogic
```

Classes Kotlin públicas passam a ficar disponíveis para Swift, como:

- `CozyTasksViewModel`
- `TaskUiState`
- `TaskFilter`
- `TaskPriority`
- `Task`
- `Category`
- `DisposableHandle`

### Arquivo principal da ponte iOS

Arquivo:

`iosApp/iosApp/ViewModels/CozyTasksObserver.swift`

Este arquivo é a ponte entre SwiftUI e Kotlin.

Ele cria o ViewModel Kotlin:

```swift
let sharedViewModel = CozyTasksViewModel()
viewModel = sharedViewModel
```

Pega o estado inicial:

```swift
state = sharedViewModel.currentState()
```

Observa mudanças do `StateFlow` Kotlin:

```swift
handle = viewModel.observeState { [weak self] next in
    DispatchQueue.main.async {
        self?.state = next
    }
}
```

O `state` é publicado para SwiftUI:

```swift
@Published var state: TaskUiState
```

Então a SwiftUI atualiza a interface quando o estado muda.

### Por que existe `observeState`

`StateFlow` é uma API Kotlin. Swift não coleta `StateFlow` de forma tão direta quanto Compose.

Por isso o Kotlin expõe este método em `CozyTasksViewModel.kt`:

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

Esse método adapta o `StateFlow` para um callback que o Swift consegue usar.

Fluxo:

```text
Kotlin StateFlow<TaskUiState>
  -> observeState(callback)
  -> Swift closure recebe TaskUiState
  -> DispatchQueue.main.async
  -> @Published state
  -> SwiftUI redesenha
```

### Como SwiftUI envia ações para Kotlin

O `CozyTasksObserver` expõe métodos simples que chamam o ViewModel Kotlin:

```swift
func openNewTask() { viewModel.openNewTask() }
func saveDraft() { viewModel.saveDraft() }
func setFilter(_ filter: TaskFilter) { viewModel.setFilter(filter: filter) }
func setCompleted(_ taskId: String, _ completed: Bool) {
    viewModel.setCompleted(taskId: taskId, isCompleted: completed)
}
```

A SwiftUI chama esses métodos.

Exemplo em uma tela SwiftUI:

```swift
FloatingAddButton(action: observer.openNewTask)
```

Ou:

```swift
CozyChip(label: "Hoje", selected: state.selectedFilter == .today) {
    observer.setFilter(.today)
}
```

Assim, a SwiftUI só envia intenção do usuário. Ela não decide como filtrar, salvar ou organizar tarefas.

## Arquivos iOS que se comunicam com o shared

### `ContentView.swift`

Arquivo:

`iosApp/iosApp/ContentView.swift`

Responsável por compor a tela principal SwiftUI.

Ele mantém:

```swift
@StateObject private var observer = CozyTasksObserver()
```

E passa `observer.state` para as views:

```swift
DashboardHeaderView(state: observer.state)
TaskListView(state: observer.state, observer: observer)
SettingsPanelView(state: observer.state, observer: observer)
```

Também abre o editor quando o estado Kotlin diz que ele deve abrir:

```swift
.sheet(isPresented: Binding(
    get: { observer.state.isEditorOpen },
    set: { if !$0 { observer.closeEditor() } }
)) {
    TaskEditorView(state: observer.state, observer: observer)
}
```

### `CozyTasksObserver.swift`

Arquivo:

`iosApp/iosApp/ViewModels/CozyTasksObserver.swift`

É o principal arquivo de comunicação com Kotlin.

Ele:

- importa `SharedLogic`.
- cria `CozyTasksViewModel`.
- observa o estado Kotlin.
- publica o estado para SwiftUI.
- encaminha ações da UI para o ViewModel Kotlin.
- cancela a observação no `deinit`.

### `DashboardSections.swift`

Arquivo:

`iosApp/iosApp/Views/DashboardSections.swift`

Lê `TaskUiState` vindo do Kotlin:

```swift
let state: TaskUiState
```

Usa campos como:

- `state.greeting`
- `state.headline`
- `state.summary.pending`
- `state.selectedFilter`

Envia filtro para Kotlin via observer:

```swift
observer.setFilter(.today)
```

### `TaskListView.swift`

Arquivo:

`iosApp/iosApp/Views/TaskListView.swift`

Lê a lista filtrada do Kotlin:

```swift
let tasks = state.visibleTasks as? [SharedLogic.Task] ?? []
```

Renderiza cada task e envia ações:

```swift
observer.editTask(task.id)
observer.setCompleted(task.id, !task.isCompleted)
```

### `TaskEditorView.swift`

Arquivo:

`iosApp/iosApp/Views/TaskEditorView.swift`

Lê o draft vindo do Kotlin:

```swift
let draft = state.draft
```

Atualiza campos chamando métodos do observer, que encaminha para Kotlin:

```swift
CozyTextField(label: "Titulo", value: draft.title, onChange: observer.updateTitle)
CozyTextField(label: "Descricao", value: draft.description, onChange: observer.updateDescription)
```

Salva ou exclui chamando:

```swift
observer.saveDraft()
observer.deleteTask(task.id)
```

### `SettingsPanelView.swift`

Arquivo:

`iosApp/iosApp/Views/SettingsPanelView.swift`

Lê configurações do estado Kotlin:

```swift
state.settings.notificationsEnabled
state.settings.completionSoundEnabled
```

E envia alterações:

```swift
observer.toggleNotifications
observer.toggleSound
```

## Fluxo Completo de uma Ação no iOS

Exemplo: usuário marca uma tarefa como concluída.

```text
1. Usuário toca no checkbox em TaskListView.swift
2. SwiftUI chama observer.setCompleted(task.id, true)
3. CozyTasksObserver chama viewModel.setCompleted(...) no Kotlin
4. CozyTasksViewModel chama ToggleTaskUseCase
5. ToggleTaskUseCase chama TaskRepository.toggleTask
6. PersistedTaskRepository atualiza a lista em memória
7. PersistedTaskRepository grava snapshot JSON no TaskLocalStorage
8. TaskLocalStorage.ios.kt salva no NSUserDefaults
9. StateFlow<TaskUiState> emite novo estado
10. observeState envia o novo TaskUiState para Swift
11. CozyTasksObserver atualiza @Published state na main thread
12. SwiftUI redesenha a lista
```

## Fluxo Completo de uma Ação no Android/Desktop

Exemplo: usuário muda o filtro para “Hoje”.

```text
1. Usuário clica no chip "Hoje" em Compose
2. FilterBar chama viewModel.setFilter(TaskFilter.TODAY)
3. CozyTasksViewModel atualiza selectedFilter
4. FilterTasksUseCase calcula visibleTasks
5. uiState emite novo TaskUiState
6. collectAsState recebe o novo estado
7. Compose recompõe CozyTasksScreen
```

## Por que a UI não tem regra de negócio

As UIs não filtram tarefas por conta própria, não calculam resumo, não decidem status e não persistem dados diretamente.

Elas apenas:

- recebem `TaskUiState`.
- mostram o estado na tela.
- enviam ações do usuário para o ViewModel.

Isso mantém a regra central em um único lugar:

`sharedLogic`

Benefícios:

- Android, Desktop e iOS têm comportamento consistente.
- Menos duplicação.
- Mais fácil testar regras no Kotlin.
- SwiftUI fica simples e nativa.
- Compose fica focado em layout e interação.

## Persistência Multiplataforma

A persistência começa no repository:

`PersistedTaskRepository.kt`

Ele transforma tarefas e categorias em:

```kotlin
TaskStoreSnapshot(
    tasks = ...,
    categories = ...
)
```

Depois serializa para JSON e chama:

```kotlin
storage.writeText(json)
```

Cada plataforma decide onde guardar:

### Android

Arquivo:

`sharedLogic/src/androidMain/kotlin/.../TaskLocalStorage.android.kt`

Usa `Context.filesDir`.

### Desktop

Arquivo:

`sharedLogic/src/jvmMain/kotlin/.../TaskLocalStorage.jvm.kt`

Usa:

`~/.cozy-tasks/cozy_tasks_store.json`

### iOS

Arquivo:

`sharedLogic/src/iosMain/kotlin/.../TaskLocalStorage.ios.kt`

Usa `NSUserDefaults`.

## Notificações

A arquitetura de notificações está preparada, mas ainda não implementa lembretes reais.

Arquivo comum:

`sharedLogic/src/commonMain/kotlin/.../notifications/ReminderScheduler.kt`

Define:

```kotlin
expect class ReminderScheduler() {
    suspend fun schedule(task: Task)
    suspend fun cancel(taskId: String)
}
```

Cada plataforma tem um `actual`:

- Android: futuro uso de `AlarmManager` ou `WorkManager`.
- iOS: futuro uso de `UNUserNotificationCenter`.
- Desktop: implementação vazia por enquanto.

## Resumo do Contrato Entre iOS e Kotlin

O contrato entre SwiftUI e Kotlin é:

```text
SwiftUI renderiza TaskUiState
SwiftUI chama métodos do CozyTasksObserver
CozyTasksObserver chama CozyTasksViewModel Kotlin
CozyTasksViewModel altera o sharedLogic
StateFlow emite novo TaskUiState
observeState entrega o estado para Swift
@Published state atualiza SwiftUI
```

Arquivos centrais desse contrato:

- Kotlin: `CozyTasksViewModel.kt`
- Kotlin: `TaskUiState.kt`
- Kotlin: `DisposableHandle.kt`
- Swift: `CozyTasksObserver.swift`
- Swift: `ContentView.swift`
- Swift: `TaskListView.swift`
- Swift: `TaskEditorView.swift`

Essa é a base que permite ter SwiftUI nativo no iOS sem duplicar regra de negócio fora do `sharedLogic`.
