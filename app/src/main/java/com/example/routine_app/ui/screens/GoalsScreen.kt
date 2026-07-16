package com.example.routine_app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.GoalType
import com.example.routine_app.data.model.ProgressEntry
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.components.LabeledProgress
import com.example.routine_app.ui.components.LineChart
import com.example.routine_app.ui.components.SectionTitle
import com.example.routine_app.ui.components.pretty
import java.time.LocalDate

@Composable
fun GoalsScreen(vm: RoutineViewModel) {
    val goals by vm.goals.collectAsStateWithLifecycle()
    val progress by vm.progress.collectAsStateWithLifecycle()
    var addProgressFor by remember { mutableStateOf<Goal?>(null) }

    val byType = goals.groupBy { it.type }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                "Metas y progreso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        if (goals.isEmpty()) {
            item { EmptyHint("Agrega metas en la pestaña Datos o importa tu plantilla.") }
        }
        GoalType.entries.forEach { type ->
            val list = byType[type].orEmpty()
            if (list.isNotEmpty()) {
                item { SectionTitle(if (type == GoalType.CALISTENIA) "💪 ${type.label}" else "📚 ${type.label}") }
                items(list, key = { "g${it.id}" }) { goal ->
                    GoalCard(
                        goal = goal,
                        entries = progress.filter { it.goalId == goal.id }.sortedBy { it.date },
                        onAddProgress = { addProgressFor = goal },
                    )
                }
            }
        }
        item { Column(modifier = Modifier.padding(bottom = 24.dp)) {} }
    }

    addProgressFor?.let { goal ->
        AddProgressDialog(
            goal = goal,
            onDismiss = { addProgressFor = null },
            onConfirm = { date, value, note ->
                vm.saveProgress(ProgressEntry(goalId = goal.id, date = date, value = value, note = note))
                // Mantén el valor actual de la meta sincronizado con el último avance.
                vm.saveGoal(goal.copy(currentValue = value))
                addProgressFor = null
            },
        )
    }
}

@Composable
private fun GoalCard(goal: Goal, entries: List<ProgressEntry>, onAddProgress: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LabeledProgress(
                label = goal.title,
                fraction = goal.progressFraction,
                trailing = "${goal.currentValue.pretty()}/${goal.targetValue.pretty()} ${goal.unit}",
            )
            AnimatedVisibility(visible = expanded) {
                Column {
                    if (entries.size >= 2) {
                        LineChart(
                            values = entries.map { it.value.toFloat() },
                            target = goal.targetValue.toFloat().takeIf { it > 0f },
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    } else {
                        Text(
                            "Registra al menos dos avances para ver la gráfica.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssistChip(
                            onClick = onAddProgress,
                            label = { Text("Registrar avance") },
                            leadingIcon = { androidx.compose.material3.Icon(Icons.Filled.Add, null) },
                        )
                        goal.deadline?.let {
                            AssistChip(onClick = {}, label = { Text("Meta: $it") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddProgressDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (date: String, value: Double, note: String) -> Unit,
) {
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var value by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val parsed = value.replace(',', '.').toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Avance · ${goal.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Valor (${goal.unit.ifBlank { "número" }})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (yyyy-MM-dd)") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = parsed != null,
                onClick = { onConfirm(date, parsed ?: 0.0, note) },
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
