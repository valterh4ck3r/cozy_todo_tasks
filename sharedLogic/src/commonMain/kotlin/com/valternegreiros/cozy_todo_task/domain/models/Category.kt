package com.valternegreiros.cozy_todo_task.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val color: String
)
