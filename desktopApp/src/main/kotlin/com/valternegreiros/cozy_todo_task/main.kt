package com.valternegreiros.cozy_todo_task

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cozy Tasks",
    ) {
        App()
    }
}
