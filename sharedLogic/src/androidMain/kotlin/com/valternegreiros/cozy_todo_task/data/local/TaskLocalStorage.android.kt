package com.valternegreiros.cozy_todo_task.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AndroidCozyStorage {
    private var appContext: Context? = null
    private var memoryFallback: String? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    internal suspend fun read(fileName: String): String? = withContext(Dispatchers.IO) {
        val context = appContext ?: return@withContext memoryFallback
        val file = File(context.filesDir, fileName)
        if (file.exists()) file.readText() else null
    }

    internal suspend fun write(fileName: String, content: String) = withContext(Dispatchers.IO) {
        val context = appContext
        if (context == null) {
            memoryFallback = content
        } else {
            File(context.filesDir, fileName).writeText(content)
        }
    }
}

actual class TaskLocalStorage actual constructor() {
    actual suspend fun readText(): String? = AndroidCozyStorage.read(FILE_NAME)
    actual suspend fun writeText(content: String) = AndroidCozyStorage.write(FILE_NAME, content)

    private companion object {
        const val FILE_NAME = "cozy_tasks_store.json"
    }
}
