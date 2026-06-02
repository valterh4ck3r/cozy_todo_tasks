import SwiftUI
import UIKit

enum CozyColor {
    static let background = adaptive(light: UIColor(red: 1.0, green: 0.906, blue: 0.722, alpha: 1), dark: UIColor(red: 0.149, green: 0.09, blue: 0.055, alpha: 1))
    static let backgroundLight = adaptive(light: UIColor(red: 1.0, green: 0.957, blue: 0.847, alpha: 1), dark: UIColor(red: 0.227, green: 0.141, blue: 0.086, alpha: 1))
    static let orange = Color(red: 0.965, green: 0.635, blue: 0.227)
    static let green = adaptive(light: UIColor(red: 0.369, green: 0.616, blue: 0.196, alpha: 1), dark: UIColor(red: 0.545, green: 0.784, blue: 0.361, alpha: 1))
    static let text = adaptive(light: UIColor(red: 0.29, green: 0.165, blue: 0.071, alpha: 1), dark: UIColor(red: 1.0, green: 0.937, blue: 0.824, alpha: 1))
    static let card = adaptive(light: UIColor(red: 1.0, green: 0.969, blue: 0.902, alpha: 1), dark: UIColor(red: 0.2, green: 0.125, blue: 0.078, alpha: 1))
    static let border = adaptive(light: UIColor(red: 0.906, green: 0.718, blue: 0.416, alpha: 1), dark: UIColor(red: 0.561, green: 0.384, blue: 0.196, alpha: 1))
    static let red = Color(red: 0.91, green: 0.365, blue: 0.165)
    static let gold = Color(red: 0.969, green: 0.776, blue: 0.29)

    private static func adaptive(light: UIColor, dark: UIColor) -> Color {
        Color(UIColor { traits in
            traits.userInterfaceStyle == .dark ? dark : light
        })
    }
}
