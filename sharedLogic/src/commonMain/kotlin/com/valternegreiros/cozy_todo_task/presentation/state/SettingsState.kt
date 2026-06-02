package com.valternegreiros.cozy_todo_task.presentation.state

data class SettingsState(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val completionSoundEnabled: Boolean = true,
    val language: String = "pt_BR",
    val futureBackupEnabled: Boolean = false
)
