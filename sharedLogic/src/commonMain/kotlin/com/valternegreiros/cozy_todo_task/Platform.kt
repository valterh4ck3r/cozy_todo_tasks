package com.valternegreiros.cozy_todo_task

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform