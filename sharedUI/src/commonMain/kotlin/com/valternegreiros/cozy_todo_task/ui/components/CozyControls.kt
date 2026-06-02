@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.ui.theme.CozyBackgroundLight
import com.valternegreiros.cozy_todo_task.ui.theme.CozyCardCream
import com.valternegreiros.cozy_todo_task.ui.theme.CozyGreen
import com.valternegreiros.cozy_todo_task.ui.theme.CozyOrange
import com.valternegreiros.cozy_todo_task.ui.theme.CozyRed
import com.valternegreiros.cozy_todo_task.ui.theme.CozySoftBorder
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTextBrown
import com.valternegreiros.cozy_todo_task.ui.util.DAY
import com.valternegreiros.cozy_todo_task.ui.util.HOUR
import com.valternegreiros.cozy_todo_task.ui.util.hexColor

@Composable
internal fun CozyCard(
    modifier: Modifier = Modifier,
    background: Color = CozyCardCream,
    padding: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .border(1.dp, CozySoftBorder.copy(alpha = 0.72f), RoundedCornerShape(22.dp))
            .padding(padding.dp),
        content = content
    )
}

@Composable
internal fun CozyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    secondary: Boolean = false,
    danger: Boolean = false
) {
    val container = when {
        danger -> CozyRed
        secondary -> CozyBackgroundLight
        else -> CozyOrange
    }
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = if (secondary) CozyTextBrown else Color.White
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun CozyTextField(value: String, onChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CozyOrange,
            unfocusedBorderColor = CozySoftBorder,
            focusedContainerColor = CozyBackgroundLight,
            unfocusedContainerColor = CozyBackgroundLight,
            focusedTextColor = CozyTextBrown,
            unfocusedTextColor = CozyTextBrown
        )
    )
}

@Composable
internal fun CozyCheckbox(checked: Boolean, onChange: (Boolean) -> Unit) {
    Checkbox(
        checked = checked,
        onCheckedChange = onChange,
        colors = CheckboxDefaults.colors(checkedColor = CozyGreen, uncheckedColor = CozyTextBrown)
    )
}

@Composable
internal fun CozyDatePicker(value: Long?, onChange: (Long?) -> Unit) {
    val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
    val today = (now / DAY) * DAY
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip("Sem data", value == null) { onChange(null) }
        FilterChip("Hoje 10:00", value == today + 10 * HOUR) { onChange(today + 10 * HOUR) }
        FilterChip("Amanha 09:00", value == today + DAY + 9 * HOUR) { onChange(today + DAY + 9 * HOUR) }
        FilterChip("Semana", value == today + 7 * DAY + 9 * HOUR) { onChange(today + 7 * DAY + 9 * HOUR) }
    }
}

@Composable
internal fun PriorityChip(priority: TaskPriority, selected: Boolean, compact: Boolean = false, onClick: () -> Unit) {
    val (label, color) = when (priority) {
        TaskPriority.LOW -> "Baixa" to CozyGreen
        TaskPriority.MEDIUM -> "Media" to CozyOrange
        TaskPriority.HIGH -> "Alta" to CozyRed
    }
    FilterChip(label, selected, color, compact, onClick)
}

@Composable
internal fun CategoryChip(category: Category, selected: Boolean, compact: Boolean = false, onClick: () -> Unit) {
    FilterChip(category.name, selected, hexColor(category.color), compact, onClick)
}

@Composable
internal fun FilterChip(
    label: String,
    selected: Boolean,
    accent: Color = CozyOrange,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val bg = if (selected) accent else CozyBackgroundLight
    val fg = if (selected) Color.White else CozyTextBrown
    Text(
        label,
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(99.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = if (compact) 9.dp else 13.dp, vertical = if (compact) 4.dp else 8.dp),
        color = fg,
        fontSize = if (compact) 11.sp else 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
internal fun NavPill(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        label,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) CozyOrange else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        color = if (selected) Color.White else CozyTextBrown,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        maxLines = 1,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun FloatingAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(CozyOrange)
            .border(3.dp, Color.White.copy(alpha = 0.55f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("+", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
internal fun SectionTitle(title: String) {
    Text(title, fontSize = 22.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
}
