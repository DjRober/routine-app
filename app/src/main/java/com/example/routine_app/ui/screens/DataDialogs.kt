package com.example.routine_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import com.example.routine_app.data.model.BlockCategory
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.GoalType
import com.example.routine_app.data.model.RoutineBlock
import com.example.routine_app.data.model.Weekday

@Composable
private fun TextRow(value: String, label: String, onChange: (String) -> Unit, number: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = if (number) KeyboardOptions(keyboardType = KeyboardType.Number)
        else KeyboardOptions.Default,
    )
}

@Composable
fun BlockDialog(initial: RoutineBlock, onDismiss: () -> Unit, onSave: (RoutineBlock) -> Unit) {
    var title by remember { mutableStateOf(initial.title) }
    var weekday by remember { mutableStateOf(initial.weekday) }
    var start by remember { mutableStateOf(initial.startTime) }
    var end by remember { mutableStateOf(initial.endTime) }
    var category by remember { mutableStateOf(initial.category) }
    var notes by remember { mutableStateOf(initial.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nuevo bloque" else "Editar bloque") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextRow(title, "Actividad", { title = it })
                EnumField("Día", Weekday.entries, weekday, { it.label }, { weekday = it })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) { TextRow(start, "Inicio (HH:mm)", { start = it }) }
                    Column(Modifier.weight(1f)) { TextRow(end, "Fin (HH:mm)", { end = it }) }
                }
                EnumField("Categoría", BlockCategory.entries, category, { it.label }, { category = it })
                TextRow(notes, "Notas", { notes = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(initial.copy(title = title.trim(), weekday = weekday, startTime = start.trim(),
                        endTime = end.trim(), category = category, notes = notes.trim()))
                },
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun ExerciseDialog(initial: Exercise, onDismiss: () -> Unit, onSave: (Exercise) -> Unit) {
    var name by remember { mutableStateOf(initial.name) }
    var weekday by remember { mutableStateOf(initial.weekday) }
    var time by remember { mutableStateOf(initial.time) }
    var sets by remember { mutableStateOf(if (initial.sets > 0) initial.sets.toString() else "") }
    var reps by remember { mutableStateOf(initial.reps) }
    var notes by remember { mutableStateOf(initial.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nuevo ejercicio" else "Editar ejercicio") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextRow(name, "Ejercicio", { name = it })
                EnumField("Día", Weekday.entries, weekday, { it.label }, { weekday = it })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) { TextRow(time, "Hora (HH:mm)", { time = it }) }
                    Column(Modifier.weight(1f)) { TextRow(sets, "Series", { sets = it }, number = true) }
                }
                TextRow(reps, "Reps (ej: 8-12, 30 seg)", { reps = it })
                TextRow(notes, "Notas", { notes = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(initial.copy(name = name.trim(), weekday = weekday, time = time.trim(),
                        sets = sets.toIntOrNull() ?: 0, reps = reps.trim(), notes = notes.trim()))
                },
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun GoalDialog(initial: Goal, onDismiss: () -> Unit, onSave: (Goal) -> Unit) {
    var title by remember { mutableStateOf(initial.title) }
    var type by remember { mutableStateOf(initial.type) }
    var target by remember { mutableStateOf(if (initial.targetValue != 0.0) initial.targetValue.toString() else "") }
    var current by remember { mutableStateOf(if (initial.currentValue != 0.0) initial.currentValue.toString() else "") }
    var unit by remember { mutableStateOf(initial.unit) }
    var deadline by remember { mutableStateOf(initial.deadline ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.id == 0L) "Nueva meta" else "Editar meta") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextRow(title, "Meta", { title = it })
                EnumField("Tipo", GoalType.entries, type, { it.label }, { type = it })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) { TextRow(current, "Actual", { current = it }, number = true) }
                    Column(Modifier.weight(1f)) { TextRow(target, "Objetivo", { target = it }, number = true) }
                }
                TextRow(unit, "Unidad (reps, seg, %)", { unit = it })
                TextRow(deadline, "Fecha límite (yyyy-MM-dd)", { deadline = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(initial.copy(
                        title = title.trim(), type = type,
                        targetValue = target.replace(',', '.').toDoubleOrNull() ?: 0.0,
                        currentValue = current.replace(',', '.').toDoubleOrNull() ?: 0.0,
                        unit = unit.trim(), deadline = deadline.trim().ifBlank { null },
                    ))
                },
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
