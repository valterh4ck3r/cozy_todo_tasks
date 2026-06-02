@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.valternegreiros.cozy_todo_task.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valternegreiros.cozy_todo_task.core.i18n.CozyStringBundle
import com.valternegreiros.cozy_todo_task.domain.models.Category
import com.valternegreiros.cozy_todo_task.domain.models.Task
import com.valternegreiros.cozy_todo_task.domain.models.TaskPriority
import com.valternegreiros.cozy_todo_task.presentation.state.TaskDraft
import com.valternegreiros.cozy_todo_task.presentation.viewmodels.CozyTasksViewModel
import com.valternegreiros.cozy_todo_task.ui.theme.CozyTheme
import com.valternegreiros.cozy_todo_task.ui.util.formatDueDate

@Composable
internal fun TaskCard(
    task: Task,
    categories: List<Category>,
    strings: CozyStringBundle,
    viewModel: CozyTasksViewModel
) {
    val colors = CozyTheme.colors
    val completedColor by animateColorAsState(if (task.isCompleted) colors.completed else colors.cardCream)
    val scale by animateFloatAsState(if (task.isCompleted) 0.985f else 1f)
    val category = categories.firstOrNull { it.id == task.categoryId }

    CozyCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { viewModel.editTask(task.id) },
        background = completedColor,
        padding = 12
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CozyCheckbox(task.isCompleted) { viewModel.setCompleted(task.id, it) }
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) colors.greenDark else colors.textBrown,
                    fontSize = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    Text(formatDueDate(task.dueDate, strings.noDate), fontSize = 12.sp)
                    PriorityChip(task.priority, selected = false, strings = strings, compact = true) {}
                    if (category != null) {
                        CategoryChip(category, selected = false, compact = true) {}
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskEditor(
    draft: TaskDraft,
    categories: List<Category>,
    selectedTask: Task?,
    strings: CozyStringBundle,
    viewModel: CozyTasksViewModel
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x77000000))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                CozyCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (selectedTask == null) strings.newTask else strings.details,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        CozyButton(strings.close, onClick = viewModel::closeEditor, secondary = true)
                    }
                    Spacer(Modifier.height(12.dp))
                    CozyTextField(draft.title, viewModel::updateDraftTitle, strings.title)
                    CozyTextField(draft.description, viewModel::updateDraftDescription, strings.description)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PriorityChip(TaskPriority.LOW, draft.priority == TaskPriority.LOW, strings = strings) { viewModel.updateDraftPriority(TaskPriority.LOW) }
                        PriorityChip(TaskPriority.MEDIUM, draft.priority == TaskPriority.MEDIUM, strings = strings) { viewModel.updateDraftPriority(TaskPriority.MEDIUM) }
                        PriorityChip(TaskPriority.HIGH, draft.priority == TaskPriority.HIGH, strings = strings) { viewModel.updateDraftPriority(TaskPriority.HIGH) }
                    }
                    Spacer(Modifier.height(8.dp))
                    CozyDatePicker(draft.dueDate, strings, viewModel::updateDraftDueDate)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach { category ->
                            CategoryChip(category, draft.categoryId == category.id) {
                                viewModel.updateDraftCategory(category.id)
                            }
                        }
                    }
                    CozyTextField(draft.notes, viewModel::updateDraftNotes, strings.notes)
                    CozyTextField(draft.checklistText, viewModel::updateDraftChecklist, strings.checklistHint)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (selectedTask != null) {
                            CozyButton(
                                strings.delete,
                                onClick = { viewModel.removeTask(selectedTask.id) },
                                danger = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        CozyButton(strings.save, onClick = viewModel::saveDraft, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun EmptyState(title: String, body: String) {
    val colors = CozyTheme.colors
    CozyCard(Modifier.fillMaxWidth(), background = colors.backgroundLight) {
        PlantPot(Modifier.size(72.dp).align(Alignment.CenterHorizontally))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(body, modifier = Modifier.align(Alignment.CenterHorizontally).alpha(0.76f))
    }
}
