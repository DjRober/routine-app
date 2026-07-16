package com.example.routine_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.BlockCategory
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.RoutineBlock
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.components.LabeledProgress
import com.example.routine_app.ui.components.SectionTitle
import com.example.routine_app.ui.components.pretty
import java.time.LocalDate

@Composable
fun TodayScreen(vm: RoutineViewModel) {
    val today = Weekday.today()
    val blocks by vm.blocks.collectAsStateWithLifecycle()
    val exercises by vm.exercises.collectAsStateWithLifecycle()
    val goals by vm.goals.collectAsStateWithLifecycle()

    val todayBlocks = blocks.filter { it.weekday == today }.sortedBy { it.startTime }
    val todayExercises = exercises.filter { it.weekday == today }.sortedBy { it.time }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                Text(
                    "Hoy es ${today.label}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    LocalDate.now().toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item { SectionTitle("🗓️ Rutina de hoy") }
        if (todayBlocks.isEmpty()) {
            item { EmptyHint("Sin bloques para hoy. Agrégalos en la pestaña Datos.") }
        } else {
            items(todayBlocks, key = { "b${it.id}" }) { BlockCard(it) }
        }

        item { SectionTitle("💪 Calistenia de hoy") }
        if (todayExercises.isEmpty()) {
            item { EmptyHint("Sin ejercicios para hoy.") }
        } else {
            items(todayExercises, key = { "e${it.id}" }) { ExerciseCard(it) }
        }

        item { SectionTitle("🎯 Progreso de metas") }
        if (goals.isEmpty()) {
            item { EmptyHint("Aún no tienes metas registradas.") }
        } else {
            items(goals, key = { "g${it.id}" }) { goal ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    LabeledProgress(
                        label = goal.title,
                        fraction = goal.progressFraction,
                        trailing = "${goal.currentValue.pretty()}/${goal.targetValue.pretty()} ${goal.unit}",
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
        item { Column(modifier = Modifier.padding(bottom = 24.dp)) {} }
    }
}

@Composable
fun BlockCard(block: RoutineBlock) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CategoryDot(block.category)
            Column(modifier = Modifier.weight(1f)) {
                Text(block.title, fontWeight = FontWeight.SemiBold)
                val time = listOf(block.startTime, block.endTime).filter { it.isNotBlank() }.joinToString(" – ")
                if (time.isNotBlank() || block.notes.isNotBlank()) {
                    Text(
                        listOfNotNull(time.ifBlank { null }, block.notes.ifBlank { null }).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(ex: Exercise) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ex.name, fontWeight = FontWeight.SemiBold)
                val detail = buildString {
                    if (ex.sets > 0) append("${ex.sets} series")
                    if (ex.reps.isNotBlank()) { if (isNotEmpty()) append(" × "); append(ex.reps) }
                }
                if (detail.isNotBlank()) {
                    Text(detail, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (ex.time.isNotBlank()) {
                Text(ex.time, style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CategoryDot(category: BlockCategory) {
    val color = when (category) {
        BlockCategory.ESTUDIO -> MaterialTheme.colorScheme.primary
        BlockCategory.EJERCICIO -> MaterialTheme.colorScheme.tertiary
        BlockCategory.PERSONAL -> MaterialTheme.colorScheme.secondary
        BlockCategory.OTRO -> MaterialTheme.colorScheme.outline
    }
    Surface(shape = RoundedCornerShape(50), color = color, modifier = Modifier.padding(2.dp)) {
        androidx.compose.foundation.layout.Box(Modifier.padding(6.dp)) {}
    }
}
