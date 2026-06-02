package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
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
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.presentation.state.SettingsState
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
internal fun SettingsPanel(
    settings: SettingsState,
    strings: CozyStringBundle,
    viewModel: CozyTasksViewModel,
    modifier: Modifier
) {
    CozyCard(modifier) {
        Text(strings.settings, fontSize = 20.sp, fontWeight = FontWeight.Black)
        SettingsRow(strings.darkTheme, settings.darkTheme) { viewModel.toggleDarkTheme(it) }
        SettingsRow(strings.notifications, settings.notificationsEnabled) { viewModel.toggleNotifications(it) }
        SettingsRow(strings.completionSound, settings.completionSoundEnabled) { viewModel.toggleCompletionSound(it) }
        Text(strings.language, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LanguageOption("en_US", settings.language, viewModel)
            LanguageOption("pt_BR", settings.language, viewModel)
            LanguageOption("es_ES", settings.language, viewModel)
        }
        Text(strings.futureBackup, fontSize = 12.sp, modifier = Modifier.alpha(0.72f))
    }
}

@Composable
internal fun SettingsRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    val colors = CozyTheme.colors
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
                checkedThumbColor = colors.green,
                checkedTrackColor = colors.switchTrack,
                uncheckedThumbColor = colors.textBrown.copy(alpha = 0.55f),
                uncheckedTrackColor = colors.backgroundLight,
                uncheckedBorderColor = colors.softBorder
            )
        )
    }
}

@Composable
private fun LanguageOption(language: String, selectedLanguage: String, viewModel: CozyTasksViewModel) {
    FilterChip(
        label = language,
        selected = selectedLanguage == language,
        onClick = { viewModel.setLanguage(language) }
    )
}
