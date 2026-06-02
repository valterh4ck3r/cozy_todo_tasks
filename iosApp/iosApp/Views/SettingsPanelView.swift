import SwiftUI
import SharedLogic

struct SettingsPanelView: View {
    let state: TaskUiState
    let observer: CozyTasksObserver

    var body: some View {
        CozyCard {
            VStack(alignment: .leading, spacing: 10) {
                Text("Configuracoes").font(.title3.weight(.black))
                Toggle("Tema escuro", isOn: Binding(get: { state.settings.darkTheme }, set: observer.toggleDark))
                Toggle("Notificacoes", isOn: Binding(get: { state.settings.notificationsEnabled }, set: observer.toggleNotifications))
                Toggle("Som ao concluir", isOn: Binding(get: { state.settings.completionSoundEnabled }, set: observer.toggleSound))
                Text("Idioma futuro: \(state.settings.futureLanguage)")
                    .font(.caption)
                    .foregroundStyle(CozyColor.text.opacity(0.65))
                Text("Backup/exportacao futura")
                    .font(.caption)
                    .foregroundStyle(CozyColor.text.opacity(0.65))
            }
        }
    }
}
