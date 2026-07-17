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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.components.EmptyHint
import com.example.routine_app.ui.theme.LocalAppColors

@Composable
fun HorarioTab(vm: RoutineViewModel, section: Section, day: Weekday) {
    val c = LocalAppColors.current
    val schedule by vm.schedule.collectAsStateWithLifecycle()
    var editItem by remember { mutableStateOf<ScheduleItem?>(null) }

    val dayItems = schedule.filter { it.weekday == day }.sortedBy { it.startTime }

    LazyColumn {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, c.line, RoundedCornerShape(10.dp))
                    .background(c.surface)
                    .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 4.dp),
            ) {
                if (dayItems.isEmpty()) EmptyHint("Sin bloques en el horario de ${day.label}.")
                dayItems.forEachIndexed { i, item ->
                    TimelineRow(
                        item = item,
                        focus = item.tag.isFocusOf(section),
                        isLast = i == dayItems.lastIndex,
                        onClick = { editItem = item },
                    )
                }
            }
        }
        item {
            AddRow("＋ Agregar bloque", onClick = { editItem = ScheduleItem(weekday = day, startTime = "", title = "") })
        }
    }

    editItem?.let { it0 ->
        ScheduleDialog(
            it0,
            onDismiss = { editItem = null },
            onSave = { vm.saveSchedule(it); editItem = null },
            onDelete = if (it0.id != 0L) ({ vm.deleteSchedule(it0); editItem = null }) else null,
        )
    }
}

@Composable
private fun TimelineRow(item: ScheduleItem, focus: Boolean, isLast: Boolean, onClick: () -> Unit) {
    val c = LocalAppColors.current
    val time = listOf(item.startTime, item.endTime).filter { it.isNotBlank() }.joinToString(" – ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .then(if (focus) Modifier.background(c.accentSoft) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Riel con punto y línea vertical.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(3.dp))
            Box(
                Modifier
                    .size(if (focus) 11.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (focus) c.accent else c.muted),
            )
            if (!isLast) Box(Modifier.width(1.dp).height(28.dp).background(c.line))
        }
        Column(Modifier.weight(1f).padding(bottom = 14.dp)) {
            Text(
                time.ifBlank { "—" },
                color = if (focus) c.accent else c.onBg,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (focus) FontWeight.Bold else FontWeight.SemiBold,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    item.title,
                    color = if (focus) c.onBg else c.muted,
                    fontSize = 12.sp,
                    fontWeight = if (focus) FontWeight.Bold else FontWeight.Normal,
                )
                if (focus) FocusBadge()
            }
        }
    }
}

@Composable
private fun FocusBadge() {
    val c = LocalAppColors.current
    Box(
        Modifier.clip(RoundedCornerShape(4.dp)).background(c.accent).padding(horizontal = 5.dp, vertical = 1.dp),
    ) {
        Text("foco activo", color = c.onAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}
