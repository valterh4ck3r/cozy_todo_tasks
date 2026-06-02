import SwiftUI
import SharedLogic

struct BottomTabsView: View {
    let selectedFilter: TaskFilter
    let observer: CozyTasksObserver

    var body: some View {
        HStack(spacing: 6) {
            tab("Hoje", .today)
            tab("Todas", .all)
            tab("Prox.", .upcoming)
            tab("Mais", .completed)
        }
        .frame(height: 56)
        .padding(.horizontal, 8)
        .padding(.vertical, 8)
        .frame(maxWidth: .infinity)
        .background(CozyColor.card)
        .overlay(Rectangle().stroke(CozyColor.border.opacity(0.55)))
    }

    private func tab(_ label: String, _ filter: TaskFilter) -> some View {
        Button(label) { observer.setFilter(filter) }
            .font(.caption.weight(.bold))
            .lineLimit(1)
            .frame(maxWidth: .infinity)
            .foregroundStyle(selectedFilter == filter ? .white : CozyColor.text)
            .padding(.vertical, 9)
            .background(selectedFilter == filter ? CozyColor.orange : .clear)
            .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
