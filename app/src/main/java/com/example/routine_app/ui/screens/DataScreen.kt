package com.example.routine_app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.BlockCategory
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.GoalType
import com.example.routine_app.data.model.RoutineBlock
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.ImportState
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.components.SectionTitle
import com.example.routine_app.ui.components.pretty

@Composable
fun DataScreen(vm: RoutineViewModel) {
    val blocks by vm.blocks.collectAsStateWithLifecycle()
    val exercises by vm.exercises.collectAsStateWithLifecycle()
    val goals by vm.goals.collectAsStateWithLifecycle()

    var tab by remember { mutableStateOf(0) }
    var importState by remember { mutableStateOf<ImportState>(ImportState.Idle) }

    var editBlock by remember { mutableStateOf<RoutineBlock?>(null) }
    var editExercise by remember { mutableStateOf<Exercise?>(null) }
    var editGoal by remember { mutableStateOf<Goal?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) vm.importFromUri(uri) { importState = it }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                "Datos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        item { ImportCard(importState) { picker.launch(XLSX_MIME_TYPES) } }

        item { SectionTitle("Editar manualmente") }
        item {
            TabRow(selectedTabIndex = tab) {
                listOf("Rutina", "Ejercicios", "Metas").forEachIndexed { i, title ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
                }
            }
        }

        when (tab) {
            0 -> {
                item { AddButton("Agregar bloque") { editBlock = RoutineBlock(title = "", weekday = Weekday.today(), startTime = "", endTime = "") } }
                if (blocks.isEmpty()) item { EmptyHint("Sin bloques.") }
                else items(blocks, key = { "b${it.id}" }) { b ->
                    EditableRow(
                        title = b.title,
                        subtitle = "${b.weekday.short} · ${listOf(b.startTime, b.endTime).filter { it.isNotBlank() }.joinToString("–")} · ${b.category.label}",
                        onClick = { editBlock = b },
                        onDelete = { vm.deleteBlock(b) },
                    )
                }
            }
            1 -> {
                item { AddButton("Agregar ejercicio") { editExercise = Exercise(name = "", weekday = Weekday.today()) } }
                if (exercises.isEmpty()) item { EmptyHint("Sin ejercicios.") }
                else items(exercises, key = { "e${it.id}" }) { e ->
                    EditableRow(
                        title = e.name,
                        subtitle = "${e.weekday.short} · ${if (e.sets > 0) "${e.sets}x" else ""}${e.reps}".trim(),
                        onClick = { editExercise = e },
                        onDelete = { vm.deleteExercise(e) },
                    )
                }
            }
            else -> {
                item { AddButton("Agregar meta") { editGoal = Goal(title = "") } }
                if (goals.isEmpty()) item { EmptyHint("Sin metas.") }
                else items(goals, key = { "g${it.id}" }) { g ->
                    EditableRow(
                        title = g.title,
                        subtitle = "${g.type.label} · ${g.currentValue.pretty()}/${g.targetValue.pretty()} ${g.unit}",
                        onClick = { editGoal = g },
                        onDelete = { vm.deleteGoal(g) },
                    )
                }
            }
        }
        item { Column(modifier = Modifier.padding(bottom = 24.dp)) {} }
    }

    editBlock?.let { b ->
        BlockDialog(b, onDismiss = { editBlock = null }, onSave = { vm.saveBlock(it); editBlock = null })
    }
    editExercise?.let { e ->
        ExerciseDialog(e, onDismiss = { editExercise = null }, onSave = { vm.saveExercise(it); editExercise = null })
    }
    editGoal?.let { g ->
        GoalDialog(g, onDismiss = { editGoal = null }, onSave = { vm.saveGoal(it); editGoal = null })
    }
}

private val XLSX_MIME_TYPES = arrayOf(
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-excel",
    "application/octet-stream",
)

@Composable
private fun ImportCard(state: ImportState, onImport: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Importar plantilla de Excel", fontWeight = FontWeight.Bold)
            Text(
                "Selecciona tu archivo .xlsx con las hojas Rutina, Ejercicios, Metas y Progreso. Reemplaza los datos actuales.",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = onImport) {
                Icon(Icons.Filled.FileUpload, null)
                Text("  Elegir archivo", )
            }
            when (state) {
                is ImportState.Loading -> Text("Importando…", style = MaterialTheme.typography.bodySmall)
                is ImportState.Success -> {
                    val d = state.result.data
                    Text(
                        "✅ Importado: ${d.blocks.size} bloques, ${d.exercises.size} ejercicios, ${d.goals.size} metas, ${d.progress.size} avances.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    state.result.warnings.take(5).forEach {
                        Text("⚠️ $it", style = MaterialTheme.typography.bodySmall)
                    }
                }
                is ImportState.Error -> Text("❌ ${state.message}", style = MaterialTheme.typography.bodySmall)
                ImportState.Idle -> {}
            }
        }
    }
}

@Composable
private fun AddButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(text) }
}

@Composable
private fun EditableRow(title: String, subtitle: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f).clickable(onClick = onClick).padding(vertical = 8.dp)) {
                Text(title.ifBlank { "(sin título)" }, fontWeight = FontWeight.Medium)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

/** Selector desplegable genérico para enums. */
@Composable
fun <T> EnumField(
    label: String,
    options: List<T>,
    selected: T,
    display: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
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
