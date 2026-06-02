import SwiftUI
import SharedLogic

struct TaskListView: View {
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        let tasks = state.visibleTasks as? [SharedLogic.Task] ?? []
        VStack(spacing: 10) {
            if tasks.isEmpty {
                EmptyTasksView()
            } else {
                ForEach(tasks, id: \.id) { task in
                    TaskRowView(task: task, state: state, observer: observer)
                }
            }
        }
    }
}

private struct EmptyTasksView: View {
    var body: some View {
        CozyCard {
            PlantView().frame(width: 70, height: 70)
            Text("Tudo calmo").font(.title3.weight(.black))
            Text("Sem tarefas nesse filtro.").foregroundStyle(CozyColor.text.opacity(0.7))
        }
    }
}

private struct TaskRowView: View {
    let task: SharedLogic.Task
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        let categories = state.categories as? [Category] ?? []
        let category = categories.first(where: { $0.id == task.categoryId })

        Button { observer.editTask(task.id) } label: {
            HStack(spacing: 10) {
                Button { observer.setCompleted(task.id, !task.isCompleted) } label: {
                    Image(systemName: task.isCompleted ? "checkmark.square.fill" : "square")
                        .foregroundStyle(task.isCompleted ? CozyColor.green : CozyColor.text)
                        .font(.title2)
                }
                VStack(alignment: .leading, spacing: 6) {
                    Text(task.title)
                        .font(.system(size: 17, weight: .bold))
                        .lineLimit(2)
                        .minimumScaleFactor(0.9)
                        .strikethrough(task.isCompleted)
                    HStack {
                        Text(formatDue(task.dueDate)).font(.caption)
                        Text(priorityLabel(task.priority))
                            .font(.caption.weight(.bold))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 3)
                            .background(priorityColor(task.priority).opacity(0.22))
                            .clipShape(Capsule())
                        if let category {
                            Text(category.name).font(.caption.weight(.bold))
                        }
                    }
                    .lineLimit(1)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                Spacer()
            }
            .padding(12)
            .frame(maxWidth: .infinity)
            .background(task.isCompleted ? CozyColor.green.opacity(0.16) : CozyColor.card)
            .clipShape(RoundedRectangle(cornerRadius: 20))
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(CozyColor.border.opacity(0.65)))
        }
        .buttonStyle(.plain)
    }
}
