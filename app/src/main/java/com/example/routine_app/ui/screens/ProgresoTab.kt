package com.example.routine_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.MilestoneStatus
import com.example.routine_app.data.model.Section
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.components.Overline
import com.example.routine_app.ui.theme.LocalAppColors

@Composable
fun ProgresoTab(vm: RoutineViewModel, section: Section) {
    val c = LocalAppColors.current
    val milestones by vm.milestones.collectAsStateWithLifecycle()
    var editMilestone by remember { mutableStateOf<Milestone?>(null) }

    val tracks = milestones
        .filter { it.section == section }
        .groupBy { it.track }
        .toList()
        .sortedBy { it.first }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (tracks.isEmpty()) {
            item { EmptyHint("Aún no hay hitos en ${section.label}. Agrega uno abajo o importa tu plantilla.") }
        }
        tracks.forEach { (track, items) ->
            item(key = "t_$track") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, c.line, RoundedCornerShape(10.dp))
                        .background(c.surface)
                        .padding(12.dp),
                ) {
                    Overline("$track · fases")
                    Spacer(Modifier.height(10.dp))
                    items.sortedBy { it.orderIndex }.forEach { m ->
                        MilestoneRow(
                            m,
                            onCycle = { vm.cycleMilestone(m) },
                            onEdit = { editMilestone = m },
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
        item {
            AddRow("＋ Agregar hito", onClick = {
                val defaultTrack = tracks.firstOrNull()?.first ?: ""
                editMilestone = Milestone(section = section, track = defaultTrack, title = "")
            })
        }
        item {
            Text("Se guarda automáticamente", color = c.muted, fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }

    editMilestone?.let { m ->
        MilestoneDialog(
            m,
            onDismiss = { editMilestone = null },
            onSave = { vm.saveMilestone(it); editMilestone = null },
            onDelete = if (m.id != 0L) ({ vm.deleteMilestone(m); editMilestone = null }) else null,
        )
    }
}

@Composable
private fun MilestoneRow(m: Milestone, onCycle: () -> Unit, onEdit: () -> Unit) {
    val c = LocalAppColors.current
    val done = m.status == MilestoneStatus.DONE
    val current = m.status == MilestoneStatus.CURRENT

    val bg = when {
        done -> c.accentSoft
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .then(if (current) Modifier.border(1.dp, c.meta, RoundedCornerShape(6.dp)) else Modifier)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(3.dp))
                .then(
                    when {
                        done -> Modifier.background(c.done)
                        current -> Modifier.border(2.dp, c.meta, RoundedCornerShape(3.dp))
                        else -> Modifier.border(2.dp, c.line, RoundedCornerShape(3.dp))
                    },
                )
                .clickable { onCycle() },
            contentAlignment = Alignment.Center,
        ) {
            if (done) Text("✓", color = c.onAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            m.title.ifBlank { "(sin título)" },
            color = when {
                done -> c.done
                current -> c.onBg
                else -> c.muted
            },
            fontSize = 12.sp,
            fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal,
            textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f).clickable { onEdit() },
        )
        if (current) {
            Text("META", color = c.meta, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
