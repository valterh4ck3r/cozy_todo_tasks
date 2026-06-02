import SwiftUI
import SharedLogic

struct DashboardHeaderView: View {
    let state: TaskUiState

    var body: some View {
        CozyCard(padding: 14) {
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 5) {
                    Text("\(state.greeting)!")
                        .font(.system(size: 16, weight: .semibold))
                        .lineLimit(1)
                    Text(state.headline)
                        .font(.system(size: 26, weight: .black))
                        .lineLimit(2)
                        .minimumScaleFactor(0.86)
                        .foregroundStyle(CozyColor.text)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                Spacer()
                PlantView().frame(width: 66, height: 66)
            }
        }
    }
}

struct DashboardSummaryView: View {
    let state: TaskUiState

    var body: some View {
        HStack(spacing: 8) {
            SummaryTile(label: "Pend.", value: state.summary.pending, color: CozyColor.orange)
            SummaryTile(label: "Concl.", value: state.summary.completed, color: CozyColor.green)
            SummaryTile(label: "Atras.", value: state.summary.overdue, color: CozyColor.red)
            SummaryTile(label: "Hoje", value: state.summary.today, color: CozyColor.gold)
        }
    }
}

struct TaskFilterBarView: View {
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                CozyChip(label: "Todas", selected: state.selectedFilter == .all) { observer.setFilter(.all) }
                CozyChip(label: "Hoje", selected: state.selectedFilter == .today) { observer.setFilter(.today) }
                CozyChip(label: "Proximas", selected: state.selectedFilter == .upcoming) { observer.setFilter(.upcoming) }
                CozyChip(label: "Concluidas", selected: state.selectedFilter == .completed) { observer.setFilter(.completed) }
                CozyChip(label: "Atrasadas", selected: state.selectedFilter == .overdue) { observer.setFilter(.overdue) }
            }
            .padding(.vertical, 1)
        }
    }
}

struct SectionTitleView: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.system(size: 24, weight: .black))
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}
