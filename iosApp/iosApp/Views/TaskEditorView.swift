import SwiftUI
import SharedLogic

struct TaskEditorView: View {
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        let draft = state.draft
        let categories = state.categories as? [Category] ?? []

        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                HStack {
                    Text(state.selectedTask == nil ? state.strings.newTask : state.strings.details)
                        .font(.title2.weight(.black))
                    Spacer()
                    Button(state.strings.close, action: observer.closeEditor)
                }

                CozyTextField(label: state.strings.title, value: draft.title, onChange: observer.updateTitle)
                CozyTextField(label: state.strings.description, value: draft.description, onChange: observer.updateDescription)

                HStack {
                    CozyChip(label: state.strings.priorityLow, selected: draft.priority == .low) { observer.updatePriority(.low) }
                    CozyChip(label: state.strings.priorityMedium, selected: draft.priority == .medium) { observer.updatePriority(.medium) }
                    CozyChip(label: state.strings.priorityHigh, selected: draft.priority == .high) { observer.updatePriority(.high) }
                }

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(categories, id: \.id) { category in
                            CozyChip(label: category.name, selected: draft.categoryId == category.id) {
                                observer.updateCategory(category.id)
                            }
                        }
                    }
                }

                CozyTextField(label: state.strings.notes, value: draft.notes, onChange: observer.updateNotes)
                CozyTextField(label: state.strings.checklistHint, value: draft.checklistText, onChange: observer.updateChecklist)

                HStack {
                    if let task = state.selectedTask {
                        Button(state.strings.delete) { observer.deleteTask(task.id) }
                            .buttonStyle(.borderedProminent)
                            .tint(CozyColor.red)
                    }
                    Button(state.strings.save, action: observer.saveDraft)
                        .buttonStyle(.borderedProminent)
                        .tint(CozyColor.orange)
                }
            }
            .padding(20)
        }
        .background(CozyColor.backgroundLight)
    }
}
