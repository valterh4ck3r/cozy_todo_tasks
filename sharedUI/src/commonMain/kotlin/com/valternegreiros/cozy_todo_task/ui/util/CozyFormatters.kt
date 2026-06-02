package com.valternegreiros.cozy_todo_task.ui.util

import androidx.compose.ui.graphics.Color
import com.valternegreiros.cozy_todo_task.ui.theme.CozyOrange

internal const val HOUR = 60 * 60 * 1000L
internal const val DAY = 24 * HOUR

internal fun formatDueDate(value: Long?): String {
    if (value == null) return "Sem data"
    val hour = ((value % DAY) / HOUR).toInt()
    val minute = ((value % HOUR) / 60_000L).toInt()
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

internal fun hexColor(hex: String): Color {
    val value = hex.removePrefix("#").toLongOrNull(16) ?: return CozyOrange
    return Color(0xFF00000000 or value)
}
