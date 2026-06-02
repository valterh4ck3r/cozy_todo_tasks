package com.valternegreiros.cozy_todo_task.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val completionSoundEnabled: Boolean = true,
    val language: String = "pt_BR",
    val futureBackupEnabled: Boolean = false
)
