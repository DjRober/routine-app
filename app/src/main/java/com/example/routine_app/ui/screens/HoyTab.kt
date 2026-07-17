package com.example.routine_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.MilestoneStatus
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Task
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.theme.LocalAppColors

@Composable
fun HoyTab(vm: RoutineViewModel, section: Section, day: Weekday) {
    val c = LocalAppColors.current
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val exercises by vm.exercises.collectAsStateWithLifecycle()
    val milestones by vm.milestones.collectAsStateWithLifecycle()

    var editTask by remember { mutableStateOf<Task?>(null) }
    var editExercise by remember { mutableStateOf<Exercise?>(null) }

    val isToday = day == Weekday.today()
    val overline = "${day.label}${if (isToday) " · hoy" else ""}"

    val headerTitle = if (section == Section.ESTUDIO) {
        milestones.firstOrNull { it.section == Section.ESTUDIO && it.status == MilestoneStatus.CURRENT }?.track
            ?: milestones.firstOrNull { it.section == Section.ESTUDIO }?.track
            ?: "Tu jornada de estudio"
    } else {
        "Entrenamiento"
    }

    val dayTasks = tasks.filter { it.section == Section.ESTUDIO && it.weekday == day }.sortedBy { it.orderIndex }
    val dayExercises = exercises.filter { it.weekday == day }.sortedBy { it.time }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        item {
            // Tarjeta con encabezado de color + cuerpo con las filas.
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, c.line, RoundedCornerShape(10.dp)),
            ) {
                Column(Modifier.fillMaxWidth().background(c.accent).padding(12.dp)) {
                    Text(overline.uppercase(), color = c.onAccent.copy(alpha = 0.85f),
                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Text(headerTitle, color = c.onAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.fillMaxWidth().background(c.surface).padding(horizontal = 12.dp)) {
                    if (section == Section.ESTUDIO) {
                        if (dayTasks.isEmpty()) EmptyHint("Sin tareas para este día.")
                        dayTasks.forEachIndexed { i, t ->
                            TaskRow(i + 1, t, onToggle = { vm.toggleTask(t) }, onEdit = { editTask = t },
                                last = i == dayTasks.lastIndex)
                        }
                    } else {
                        if (dayExercises.isEmpty()) EmptyHint("Sin ejercicios para este día.")
                        dayExercises.forEachIndexed { i, e ->
                            ExerciseRow(e, onToggle = { vm.toggleExercise(e) }, onEdit = { editExercise = e },
                                last = i == dayExercises.lastIndex)
                        }
                    }
                }
            }
        }
        item {
            AddRow(
                if (section == Section.ESTUDIO) "＋ Agregar tarea" else "＋ Agregar ejercicio",
                onClick = {
                    if (section == Section.ESTUDIO)
                        editTask = Task(weekday = day, title = "", orderIndex = dayTasks.size + 1)
                    else
                        editExercise = Exercise(weekday = day, name = "")
                },
            )
        }
    }

    editTask?.let { t ->
        TaskDialog(t, onDismiss = { editTask = null }, onSave = { vm.saveTask(it); editTask = null },
            onDelete = if (t.id != 0L) ({ vm.deleteTask(t); editTask = null }) else null)
    }
    editExercise?.let { e ->
        ExerciseDialog(e, onDismiss = { editExercise = null }, onSave = { vm.saveExercise(it); editExercise = null },
            onDelete = if (e.id != 0L) ({ vm.deleteExercise(e); editExercise = null }) else null)
    }
}

@Composable
private fun TaskRow(number: Int, task: Task, onToggle: () -> Unit, onEdit: () -> Unit, last: Boolean) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .then(if (task.done) Modifier.background(c.done) else Modifier.border(1.dp, c.onBg, RoundedCornerShape(4.dp)))
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Text(if (task.done) "✓" else "$number",
                color = if (task.done) c.onAccent else c.onBg,
                fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Text(
            task.title.ifBlank { "(sin título)" },
            color = if (task.done) c.muted else c.onBg,
            fontSize = 13.sp,
            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f).clickable { onEdit() },
        )
        if (task.estimate.isNotBlank()) {
            Text(task.estimate, color = c.accent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun ExerciseRow(ex: Exercise, onToggle: () -> Unit, onEdit: () -> Unit, last: Boolean) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .then(if (ex.done) Modifier.background(c.done) else Modifier.border(1.dp, c.onBg, RoundedCornerShape(4.dp)))
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            if (ex.done) Text("✓", color = c.onAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).clickable { onEdit() }) {
            Text(ex.name.ifBlank { "(sin nombre)" },
                color = if (ex.done) c.muted else c.onBg, fontSize = 13.sp,
                textDecoration = if (ex.done) TextDecoration.LineThrough else TextDecoration.None,
                fontWeight = FontWeight.Medium)
            if (ex.detail.isNotBlank())
                Text(ex.detail, color = c.muted, fontSize = 11.sp)
        }
        if (ex.time.isNotBlank())
            Text(ex.time, color = c.accent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
internal fun AddRow(text: String, onClick: () -> Unit) {
    val c = LocalAppColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, c.line, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
