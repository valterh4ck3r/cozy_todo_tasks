import SwiftUI

struct CozyTextField: View {
    let label: String
    let value: String
    let onChange: (String) -> Void

    var body: some View {
        TextField(label, text: Binding(get: { value }, set: onChange), axis: .vertical)
            .textFieldStyle(.roundedBorder)
    }
}
