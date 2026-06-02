package com.valternegreiros.cozy_todo_task.core.i18n

enum class CozyLanguage(val tag: String) {
    EN("en"),
    PT_BR("pt-BR"),
    ES("es")
}

data class CozyStringBundle(
    val appName: String,
    val today: String,
    val all: String,
    val upcoming: String,
    val completed: String,
    val overdue: String,
    val settings: String
)

object CozyStrings {
    fun bundle(language: CozyLanguage): CozyStringBundle = when (language) {
        CozyLanguage.EN -> CozyStringBundle("Cozy Tasks", "Today", "All", "Upcoming", "Completed", "Overdue", "Settings")
        CozyLanguage.PT_BR -> CozyStringBundle("Cozy Tasks", "Hoje", "Todas", "Proximas", "Concluidas", "Atrasadas", "Configuracoes")
        CozyLanguage.ES -> CozyStringBundle("Cozy Tasks", "Hoy", "Todas", "Proximas", "Completadas", "Atrasadas", "Configuracion")
    }
}
