package com.example.routine_app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.components.DaySelector
import com.example.routine_app.ui.components.SectionSwitcher
import com.example.routine_app.ui.components.TabBar
import com.example.routine_app.ui.components.ToolButton
import com.example.routine_app.ui.screens.HorarioTab
import com.example.routine_app.ui.screens.HoyTab
import com.example.routine_app.ui.screens.ProgresoTab
import com.example.routine_app.ui.theme.LocalAppColors
import com.example.routine_app.ui.theme.ThemeId
import com.example.routine_app.ui.theme.colorsFor

private val XLSX_MIME_TYPES = arrayOf(
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-excel",
    "application/octet-stream",
)

@Composable
fun RoutineApp(vm: RoutineViewModel) {
    val c = LocalAppColors.current
    var section by remember { mutableStateOf(Section.ESTUDIO) }
    var selectedDay by remember { mutableStateOf(Weekday.today()) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val theme by vm.themeId.collectAsStateWithLifecycle()
    val importState by vm.importState.collectAsStateWithLifecycle()

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.importFromUri(uri)
    }

    Scaffold(containerColor = c.bg) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            SectionSwitcher(section, { section = it })

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToolButton("⬒ Importar Excel", { picker.launch(XLSX_MIME_TYPES) }, Modifier.weight(1f))
                ToolButton("◐ Tema: ${theme.displayName}", { showThemeDialog = true }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))
            DaySelector(selectedDay, { selectedDay = it })

            Spacer(Modifier.height(14.dp))
            TabBar(listOf("Hoy", "Horario", "Progreso"), tab, { tab = it })
            Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))

            ImportBanner(importState, onDismiss = vm::dismissImportState)

            Spacer(Modifier.height(12.dp))
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    0 -> HoyTab(vm, section, selectedDay)
                    1 -> HorarioTab(vm, section, selectedDay)
                    else -> ProgresoTab(vm, section)
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeDialog(
            current = theme,
            onSelect = { vm.setTheme(it); showThemeDialog = false },
            onDismiss = { showThemeDialog = false },
        )
    }
}

@Composable
private fun ImportBanner(state: ImportState, onDismiss: () -> Unit) {
    val c = LocalAppColors.current
    val (msg, color) = when (state) {
        is ImportState.Loading -> "Importando…" to c.muted
        is ImportState.Success -> {
            val d = state.result.data
            "✅ ${d.schedule.size} horario · ${d.tasks.size} tareas · ${d.exercises.size} ejercicios · ${d.milestones.size} hitos" to c.done
        }
        is ImportState.Error -> "❌ ${state.message}" to c.meta
        ImportState.Idle -> return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(c.accentSoft)
            .clickable { onDismiss() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(msg, color = color, fontSize = 11.sp, modifier = Modifier.weight(1f))
        if (state !is ImportState.Loading) Text("✕", color = c.muted, fontSize = 12.sp)
    }
}

@Composable
private fun ThemeDialog(current: ThemeId, onSelect: (ThemeId) -> Unit, onDismiss: () -> Unit) {
    val c = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text("Elegir tema") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeId.entries.forEach { id ->
                    val colors = colorsFor(id)
                    val on = id == current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(if (on) 1.5.dp else 1.dp, if (on) c.accent else c.line, RoundedCornerShape(8.dp))
                            .clickable { onSelect(id) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            colors.swatches.forEach { sw ->
                                Box(Modifier.size(14.dp, 22.dp).background(sw))
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text(id.displayName, color = c.onBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(id.fontLabel, color = c.muted, fontSize = 10.sp)
                        }
                        if (on) Text("✓", color = c.accent, fontSize = 14.sp)
                    }
                }
            }
        },
    )
}
