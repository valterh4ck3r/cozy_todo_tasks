import Combine
import Foundation
import SharedLogic

final class CozyTasksObserver: ObservableObject {
    private let viewModel: CozyTasksViewModel
    private var handle: DisposableHandle?

    @Published var state: TaskUiState

    init() {
        let sharedViewModel = CozyTasksViewModel()
        viewModel = sharedViewModel
        state = sharedViewModel.currentState()
        handle = viewModel.observeState { [weak self] next in
            DispatchQueue.main.async {
                self?.state = next
            }
        }
    }

    deinit {
        handle?.dispose()
        viewModel.close()
    }

    func setFilter(_ filter: TaskFilter) { viewModel.setFilter(filter: filter) }
    func openNewTask() { viewModel.openNewTask() }
    func closeEditor() { viewModel.closeEditor() }
    func saveDraft() { viewModel.saveDraft() }
    func editTask(_ taskId: String) { viewModel.editTask(taskId: taskId) }
    func deleteTask(_ taskId: String) { viewModel.removeTask(taskId: taskId) }
    func setCompleted(_ taskId: String, _ completed: Bool) { viewModel.setCompleted(taskId: taskId, isCompleted: completed) }
    func updateTitle(_ value: String) { viewModel.updateDraftTitle(value: value) }
    func updateDescription(_ value: String) { viewModel.updateDraftDescription(value: value) }
    func updateNotes(_ value: String) { viewModel.updateDraftNotes(value: value) }
    func updateChecklist(_ value: String) { viewModel.updateDraftChecklist(value: value) }
    func updatePriority(_ value: TaskPriority) { viewModel.updateDraftPriority(value: value) }
    func updateCategory(_ value: String?) { viewModel.updateDraftCategory(value: value) }
    func updateDueDate(_ value: KotlinLong?) { viewModel.updateDraftDueDate(value: value) }
    func toggleDark(_ value: Bool) { viewModel.toggleDarkTheme(value: value) }
    func toggleNotifications(_ value: Bool) { viewModel.toggleNotifications(value: value) }
    func toggleSound(_ value: Bool) { viewModel.toggleCompletionSound(value: value) }
}
