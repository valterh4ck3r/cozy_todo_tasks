package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valternegreiros.cozy_todo_task.presentation.state.SettingsState
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.theme.CozyGreen

@Composable
internal fun SettingsPanel(settings: SettingsState, viewModel: CozyTasksViewModel, modifier: Modifier) {
    CozyCard(modifier) {
        Text("Configuracoes", fontSize = 20.sp, fontWeight = FontWeight.Black)
        SettingsRow("Tema escuro", settings.darkTheme) { viewModel.toggleDarkTheme(it) }
        SettingsRow("Notificacoes", settings.notificationsEnabled) { viewModel.toggleNotifications(it) }
        SettingsRow("Som ao concluir", settings.completionSoundEnabled) { viewModel.toggleCompletionSound(it) }
        Text("Idioma futuro: ${settings.futureLanguage}", fontSize = 12.sp, modifier = Modifier.alpha(0.72f))
        Text("Backup/exportacao futura", fontSize = 12.sp, modifier = Modifier.alpha(0.72f))
    }
}

@Composable
internal fun SettingsRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Switch(
            checked = value,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CozyGreen,
                checkedTrackColor = androidx.compose.ui.graphics.Color(0xFFCFE8B7)
            )
        )
    }
}
