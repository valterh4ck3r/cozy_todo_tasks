import SwiftUI
import SharedLogic

func priorityLabel(_ priority: TaskPriority, _ strings: CozyStringBundle) -> String {
    switch priority {
    case .low: return strings.priorityLow
    case .medium: return strings.priorityMedium
    default: return strings.priorityHigh
    }
}

func priorityColor(_ priority: TaskPriority) -> Color {
    switch priority {
    case .low: return CozyColor.green
    case .medium: return CozyColor.orange
    default: return CozyColor.red
    }
}

func formatDue(_ value: KotlinLong?, noDate: String) -> String {
    guard let value else { return noDate }
    let millis = value.int64Value
    let hour = (millis % 86_400_000) / 3_600_000
    let minute = (millis % 3_600_000) / 60_000
    return String(format: "%02d:%02d", hour, minute)
}
