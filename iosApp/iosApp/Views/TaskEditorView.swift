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
                    Text(state.selectedTask == nil ? "Nova tarefa" : "Detalhes")
                        .font(.title2.weight(.black))
                    Spacer()
                    Button("Fechar", action: observer.closeEditor)
                }

                CozyTextField(label: "Titulo", value: draft.title, onChange: observer.updateTitle)
                CozyTextField(label: "Descricao", value: draft.description, onChange: observer.updateDescription)

                HStack {
                    CozyChip(label: "Baixa", selected: draft.priority == .low) { observer.updatePriority(.low) }
                    CozyChip(label: "Media", selected: draft.priority == .medium) { observer.updatePriority(.medium) }
                    CozyChip(label: "Alta", selected: draft.priority == .high) { observer.updatePriority(.high) }
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

                CozyTextField(label: "Observacoes", value: draft.notes, onChange: observer.updateNotes)
                CozyTextField(label: "Checklist opcional", value: draft.checklistText, onChange: observer.updateChecklist)

                HStack {
                    if let task = state.selectedTask {
                        Button("Excluir") { observer.deleteTask(task.id) }
                            .buttonStyle(.borderedProminent)
                            .tint(CozyColor.red)
                    }
                    Button("Salvar", action: observer.saveDraft)
                        .buttonStyle(.borderedProminent)
                        .tint(CozyColor.orange)
                }
            }
            .padding(20)
        }
        .background(CozyColor.backgroundLight)
    }
}
