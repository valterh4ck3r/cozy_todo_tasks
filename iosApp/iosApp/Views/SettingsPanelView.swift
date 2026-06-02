import SwiftUI
import SharedLogic

struct SettingsPanelView: View {
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        CozyCard {
            VStack(alignment: .leading, spacing: 10) {
                Text(state.strings.settings).font(.title3.weight(.black))
                Toggle(state.strings.darkTheme, isOn: Binding(get: { state.settings.darkTheme }, set: observer.toggleDark))
                Toggle(state.strings.notifications, isOn: Binding(get: { state.settings.notificationsEnabled }, set: observer.toggleNotifications))
                Toggle(state.strings.completionSound, isOn: Binding(get: { state.settings.completionSoundEnabled }, set: observer.toggleSound))
                Text(state.strings.language)
                    .font(.subheadline.weight(.bold))
                    .padding(.top, 8)
                HStack {
                    languageChip("en_US")
                    languageChip("pt_BR")
                    languageChip("es_ES")
                }
                Text(state.strings.futureBackup)
                    .font(.caption)
                    .foregroundStyle(CozyColor.text.opacity(0.65))
            }
        }
    }

    private func languageChip(_ language: String) -> some View {
        CozyChip(label: language, selected: state.settings.language == language) {
            observer.setLanguage(language)
        }
    }
}
