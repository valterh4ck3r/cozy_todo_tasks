import SwiftUI

struct SummaryTile: View {
    let label: String
    let value: Int32
    let color: Color

    var body: some View {
        CozyCard(padding: 10) {
            VStack(alignment: .leading, spacing: 8) {
                Text("\(value)")
                    .font(.system(size: 16, weight: .black))
                    .foregroundStyle(.white)
                    .frame(width: 32, height: 32)
                    .background(color)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                Text(label)
                    .font(.system(size: 11, weight: .semibold))
                    .lineLimit(1)
            }
        }
        .frame(height: 92)
    }
}
