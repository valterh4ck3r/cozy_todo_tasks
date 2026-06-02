package com.valternegreiros.cozy_todo_task.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class TaskLocalStorage actual constructor() {
    private val file: File by lazy {
        File(System.getProperty("user.home"), ".cozy-tasks/cozy_tasks_store.json")
    }

    actual suspend fun readText(): String? = withContext(Dispatchers.IO) {
        if (file.exists()) file.readText() else null
    }

    actual suspend fun writeText(content: String) = withContext(Dispatchers.IO) {
        file.parentFile?.mkdirs()
        file.writeText(content)
    }
}
