package com.valternegreiros.cozy_todo_task.data.local

import platform.Foundation.NSUserDefaults

actual class TaskLocalStorage actual constructor() {
    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults

    actual suspend fun readText(): String? = defaults.stringForKey(STORE_KEY)

    actual suspend fun writeText(content: String) {
        defaults.setObject(content, forKey = STORE_KEY)
        defaults.synchronize()
    }

    private companion object {
        const val STORE_KEY = "cozy_tasks_store_json"
    }
}
