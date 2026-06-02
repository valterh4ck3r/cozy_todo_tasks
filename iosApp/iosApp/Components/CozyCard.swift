import SwiftUI

struct CozyCard<Content: View>: View {
    var padding: CGFloat = 16
    var cornerRadius: CGFloat = 22
    @ViewBuilder let content: () -> Content

    init(
        padding: CGFloat = 16,
        cornerRadius: CGFloat = 22,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.padding = padding
        self.cornerRadius = cornerRadius
        self.content = content
    }

    var body: some View {
        content()
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(padding)
            .background(CozyColor.card)
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .overlay(RoundedRectangle(cornerRadius: cornerRadius).stroke(CozyColor.border.opacity(0.7)))
    }
}

struct CozyChip: View {
    let label: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(label, action: action)
            .font(.caption.weight(.bold))
            .lineLimit(1)
            .foregroundStyle(selected ? .white : CozyColor.text)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(selected ? CozyColor.orange : CozyColor.backgroundLight)
            .clipShape(Capsule())
            .overlay(Capsule().stroke(CozyColor.border.opacity(0.45)))
    }
}

struct PlantView: View {
    var body: some View {
        ZStack {
            Circle().fill(CozyColor.green).frame(width: 24, height: 24).offset(x: -16, y: -16)
            Circle().fill(CozyColor.green.opacity(0.8)).frame(width: 30, height: 30).offset(x: 8, y: -22)
            Circle().fill(CozyColor.green).frame(width: 24, height: 24).offset(x: 18, y: -2)
            RoundedRectangle(cornerRadius: 14).fill(CozyColor.orange).frame(width: 46, height: 30).offset(y: 24)
        }
    }
}
