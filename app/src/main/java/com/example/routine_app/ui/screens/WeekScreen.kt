package com.example.routine_app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
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
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.components.SectionTitle

@Composable
fun WeekScreen(vm: RoutineViewModel) {
    var selected by remember { mutableStateOf(Weekday.today()) }
    val blocks by vm.blocks.collectAsStateWithLifecycle()
    val exercises by vm.exercises.collectAsStateWithLifecycle()

    val dayBlocks = blocks.filter { it.weekday == selected }.sortedBy { it.startTime }
    val dayExercises = exercises.filter { it.weekday == selected }.sortedBy { it.time }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Weekday.entries.forEach { day ->
                FilterChip(
                    selected = day == selected,
                    onClick = { selected = day },
                    label = { Text(day.short) },
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    selected.label,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
            item { SectionTitle("🗓️ Rutina") }
            if (dayBlocks.isEmpty()) item { EmptyHint("Sin bloques este día.") }
            else items(dayBlocks, key = { "b${it.id}" }) { BlockCard(it) }

            item { SectionTitle("💪 Calistenia") }
            if (dayExercises.isEmpty()) item { EmptyHint("Sin ejercicios este día.") }
            else items(dayExercises, key = { "e${it.id}" }) { ExerciseCard(it) }

            item { Column(modifier = Modifier.padding(bottom = 24.dp)) {} }
        }
    }
}
