package com.example.routine_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Weekday
import com.example.routine_app.ui.theme.LocalAppColors

/** Switcher tipo píldora entre los dos contextos (Estudio / Calistenia). */
@Composable
fun SectionSwitcher(section: Section, onSelect: (Section) -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(c.pillBg)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Section.entries.forEach { s ->
            val on = s == section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (on) c.onBg else Color.Transparent)
                    .clickable { onSelect(s) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    s.label,
                    color = if (on) c.bg else c.muted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

/** Selector horizontal de día (L M X J V S D). */
@Composable
fun DaySelector(selected: Weekday, onSelect: (Weekday) -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Weekday.entries.forEach { d ->
            val on = d == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .then(if (on) Modifier.background(c.onBg) else Modifier.border(1.dp, c.line, RoundedCornerShape(6.dp)))
                    .clickable { onSelect(d) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    d.letter,
                    color = if (on) c.bg else c.onBg,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

/** Barra de pestañas subrayada (Hoy · Horario · Progreso). */
@Composable
fun TabBar(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        tabs.forEachIndexed { i, title ->
            val on = i == selectedIndex
            Column(
                modifier = Modifier.clickable { onSelect(i) }.padding(bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    title,
                    color = if (on) c.onBg else c.muted,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
                Box(
                    Modifier
                        .height(2.dp)
                        .width(28.dp)
                        .background(if (on) c.accent else Color.Transparent),
                )
            }
        }
    }
}

/** Botón de herramienta con borde punteado (Importar / Tema). */
@Composable
fun ToolButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(c.surface)
            .border(1.dp, c.line, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.muted, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}

/** Encabezado de sección en mayúsculas. */
@Composable
fun Overline(text: String, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Text(
        text.uppercase(),
        color = c.muted,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        modifier = modifier,
    )
}

/** Mensaje cuando no hay contenido. */
@Composable
fun EmptyHint(text: String, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Text(text, color = c.muted, fontSize = 13.sp, modifier = modifier.padding(vertical = 12.dp))
}

/** Punto pequeño de color. */
@Composable
fun Dot(color: Color, size: Int = 8) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(color))
}

fun String.strikeIf(done: Boolean): TextDecoration =
    if (done) TextDecoration.LineThrough else TextDecoration.None
