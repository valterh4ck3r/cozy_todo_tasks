package com.valternegreiros.cozy_todo_task.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object AppDispatchers {
    val background: CoroutineDispatcher = Dispatchers.Default
}
