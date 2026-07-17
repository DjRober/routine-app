package com.example.routine_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.MilestoneStatus
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.ScheduleTag
import com.example.routine_app.data.model.Task

@Composable
private fun TextRow(value: String, label: String, onChange: (String) -> Unit, number: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = if (number) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
    )
}

@Composable
private fun <T> EnumDropdown(label: String, options: List<T>, selected: T, display: (T) -> String, onSelect: (T) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(display(selected), modifier = Modifier.weight(1f))
                Icon(Icons.Filled.ArrowDropDown, null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(display(opt)) }, onClick = { onSelect(opt); open = false })
                }
            }
        }
    }
}

@Composable
private fun EditScaffold(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    saveEnabled: Boolean,
    onDelete: (() -> Unit)?,
    body: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) { body() } },
        confirmButton = { TextButton(enabled = saveEnabled, onClick = onSave) { Text("Guardar") } },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onDelete != null) TextButton(onClick = onDelete) { Text("Eliminar", color = MaterialTheme.colorScheme.secondary) }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        },
    )
}

@Composable
fun TaskDialog(initial: Task, onDismiss: () -> Unit, onSave: (Task) -> Unit, onDelete: (() -> Unit)?) {
    var title by remember { mutableStateOf(initial.title) }
    var estimate by remember { mutableStateOf(initial.estimate) }
    EditScaffold(if (initial.id == 0L) "Nueva tarea" else "Editar tarea", onDismiss,
        onSave = { onSave(initial.copy(title = title.trim(), estimate = estimate.trim())) },
        saveEnabled = title.isNotBlank(), onDelete = onDelete) {
        TextRow(title, "Tarea", { title = it })
        TextRow(estimate, "Estimación (2h, 45m)", { estimate = it })
    }
}

@Composable
fun ExerciseDialog(initial: Exercise, onDismiss: () -> Unit, onSave: (Exercise) -> Unit, onDelete: (() -> Unit)?) {
    var name by remember { mutableStateOf(initial.name) }
    var time by remember { mutableStateOf(initial.time) }
    var sets by remember { mutableStateOf(if (initial.sets > 0) initial.sets.toString() else "") }
    var reps by remember { mutableStateOf(initial.reps) }
    EditScaffold(if (initial.id == 0L) "Nuevo ejercicio" else "Editar ejercicio", onDismiss,
        onSave = { onSave(initial.copy(name = name.trim(), time = time.trim(), sets = sets.toIntOrNull() ?: 0, reps = reps.trim())) },
        saveEnabled = name.isNotBlank(), onDelete = onDelete) {
        TextRow(name, "Ejercicio", { name = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) { TextRow(time, "Hora (HH:mm)", { time = it }) }
            Column(Modifier.weight(1f)) { TextRow(sets, "Series", { sets = it }, number = true) }
        }
        TextRow(reps, "Reps (8-12, 30 seg)", { reps = it })
    }
}

@Composable
fun ScheduleDialog(initial: ScheduleItem, onDismiss: () -> Unit, onSave: (ScheduleItem) -> Unit, onDelete: (() -> Unit)?) {
    var title by remember { mutableStateOf(initial.title) }
    var start by remember { mutableStateOf(initial.startTime) }
    var end by remember { mutableStateOf(initial.endTime) }
    var tag by remember { mutableStateOf(initial.tag) }
    var notes by remember { mutableStateOf(initial.notes) }
    EditScaffold(if (initial.id == 0L) "Nuevo bloque" else "Editar bloque", onDismiss,
        onSave = { onSave(initial.copy(title = title.trim(), startTime = start.trim(), endTime = end.trim(), tag = tag, notes = notes.trim())) },
        saveEnabled = title.isNotBlank(), onDelete = onDelete) {
        TextRow(title, "Actividad", { title = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) { TextRow(start, "Inicio (HH:mm)", { start = it }) }
            Column(Modifier.weight(1f)) { TextRow(end, "Fin (HH:mm)", { end = it }) }
        }
        EnumDropdown("Contexto", ScheduleTag.entries, tag, { it.label }, { tag = it })
        TextRow(notes, "Notas", { notes = it })
    }
}

@Composable
fun MilestoneDialog(initial: Milestone, onDismiss: () -> Unit, onSave: (Milestone) -> Unit, onDelete: (() -> Unit)?) {
    var track by remember { mutableStateOf(initial.track) }
    var title by remember { mutableStateOf(initial.title) }
    var status by remember { mutableStateOf(initial.status) }
    EditScaffold(if (initial.id == 0L) "Nuevo hito" else "Editar hito", onDismiss,
        onSave = { onSave(initial.copy(track = track.trim(), title = title.trim(), status = status)) },
        saveEnabled = title.isNotBlank() && track.isNotBlank(), onDelete = onDelete) {
        TextRow(track, "Proyecto / grupo", { track = it })
        TextRow(title, "Hito / fase", { title = it })
        EnumDropdown("Estado", MilestoneStatus.entries, status, {
            when (it) { MilestoneStatus.DONE -> "Hecho"; MilestoneStatus.CURRENT -> "Actual (Meta)"; MilestoneStatus.PENDING -> "Pendiente" }
        }, { status = it })
    }
}
