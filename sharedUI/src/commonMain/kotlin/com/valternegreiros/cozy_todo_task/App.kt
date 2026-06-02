package com.valternegreiros.cozy_todo_task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.screens.CozyTasksScreen
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme
import com.valternegreiros.cozy_todo_task.ui.theme.CozyThemeProvider

@Composable
@Preview
fun App(viewModel: CozyTasksViewModel = remember { CozyTasksViewModel() }) {
    val state by viewModel.uiState.collectAsState()
    DisposableEffect(viewModel) {
        onDispose { viewModel.close() }
    }

    MaterialTheme {
        CozyThemeProvider(darkTheme = state.settings.darkTheme) {
            val colors = CozyTheme.colors
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                color = colors.background,
                contentColor = colors.textBrown
            ) {
                CozyTasksScreen(state, viewModel)
            }
        }
    }
}
