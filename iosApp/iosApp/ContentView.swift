import SwiftUI

struct ContentView: View {
    @StateObject private var observer = CozyTasksObserver()

    var body: some View {
        ZStack(alignment: .bottom) {
            CozyColor.background.ignoresSafeArea()
            ScrollView {
                VStack(spacing: 12) {
                    DashboardHeaderView(state: observer.state)
                    DashboardSummaryView(state: observer.state)
                    TaskFilterBarView(state: observer.state, observer: observer)
                    SectionTitleView(title: observer.state.strings.tasks)
                    TaskListView(state: observer.state, observer: observer)
                    SettingsPanelView(state: observer.state, observer: observer)
                    Color.clear.frame(height: 96)
                }
                .padding(.horizontal, 12)
                .padding(.top, 10)
            }
            FloatingAddButton(action: observer.openNewTask)
        }
        .preferredColorScheme(observer.state.settings.darkTheme ? .dark : .light)
        .sheet(isPresented: Binding(get: { observer.state.isEditorOpen }, set: { if !$0 { observer.closeEditor() } })) {
            TaskEditorView(state: observer.state, observer: observer)
                .presentationDetents([.medium, .large])
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
