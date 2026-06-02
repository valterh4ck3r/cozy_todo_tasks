package com.valternegreiros.cozy_todo_task.core.time

interface Clock {
    fun now(): Long
}

class SystemClock : Clock {
    override fun now(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
