package com.valternegreiros.cozy_todo_task.data.local

expect class TaskLocalStorage() {
    suspend fun readText(): String?
    suspend fun writeText(content: String)
}
