package com.example.routine_app.data.importer

import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.MilestoneStatus
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.ScheduleTag
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Task
import com.example.routine_app.data.model.Weekday
import java.io.InputStream

/** Contenido completo interpretado desde la plantilla. */
data class ImportedData(
    val schedule: List<ScheduleItem>,
    val tasks: List<Task>,
    val exercises: List<Exercise>,
    val milestones: List<Milestone>,
) {
    val total: Int get() = schedule.size + tasks.size + exercises.size + milestones.size
}

/** Resultado de una importación: datos + advertencias legibles. */
data class ImportResult(val data: ImportedData, val warnings: List<String>)

/**
 * Interpreta una plantilla .xlsx con hojas "Horario", "Tareas", "Ejercicios" e "Hitos".
 * Reconoce hojas y columnas por nombre aproximado (español), en cualquier orden, y
 * omite filas incompletas acumulando advertencias.
 */
object TemplateImporter {

    fun import(input: InputStream): ImportResult {
        val sheets = XlsxReader.read(input)
        val warnings = ArrayList<String>()

        val schedule = ArrayList<ScheduleItem>()
        val tasks = ArrayList<Task>()
        val exercises = ArrayList<Exercise>()
        val milestones = ArrayList<Milestone>()

        for ((rawName, rows) in sheets) {
            val name = rawName.trim().lowercase()
            when {
                name.startsWith("horar") || name.startsWith("timeline") || name.startsWith("rutina") ->
                    parseSchedule(rows, schedule, warnings)
                name.startsWith("tarea") || name.startsWith("task") || name.startsWith("hoy") ->
                    parseTasks(rows, tasks, warnings)
                name.startsWith("ejerci") || name.startsWith("calist") || name.startsWith("entren") ->
                    parseExercises(rows, exercises, warnings)
                name.startsWith("hito") || name.startsWith("progres") || name.startsWith("fase") || name.startsWith("mile") ->
                    parseMilestones(rows, milestones, warnings)
                else -> warnings.add("Hoja \"$rawName\" ignorada (nombre no reconocido).")
            }
        }
        return ImportResult(ImportedData(schedule, tasks, exercises, milestones), warnings)
    }

    private fun parseSchedule(rows: List<List<String>>, out: MutableList<ScheduleItem>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cDay = col(h, "día", "dia", "day")
        val cStart = col(h, "inicio", "desde", "start", "hora")
        val cEnd = col(h, "fin", "hasta", "end")
        val cTitle = col(h, "activ", "títul", "titul", "bloque", "evento", "nombre")
        val cTag = col(h, "context", "categor", "tipo", "secci")
        val cNotes = col(h, "nota", "detalle", "coment")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val day = Weekday.parse(cell(r, cDay))
            val title = cell(r, cTitle)
            if (day == null || title.isBlank()) {
                warn.add("Horario, fila ${i + 2}: falta día o actividad, omitida.")
                return@forEachIndexed
            }
            out.add(
                ScheduleItem(
                    weekday = day,
                    startTime = normalizeTime(cell(r, cStart)),
                    endTime = normalizeTime(cell(r, cEnd)),
                    title = title,
                    tag = ScheduleTag.parse(cell(r, cTag)),
                    notes = cell(r, cNotes),
                )
            )
        }
    }

    private fun parseTasks(rows: List<List<String>>, out: MutableList<Task>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cSection = col(h, "context", "secci", "área", "area")
        val cDay = col(h, "día", "dia", "day")
        val cTitle = col(h, "tarea", "activ", "títul", "titul", "task", "nombre")
        val cEstimate = col(h, "estim", "durac", "tiempo", "horas")
        val cOrder = col(h, "orden", "order", "#")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val day = Weekday.parse(cell(r, cDay))
            val title = cell(r, cTitle)
            if (day == null || title.isBlank()) {
                warn.add("Tareas, fila ${i + 2}: falta día o tarea, omitida.")
                return@forEachIndexed
            }
            out.add(
                Task(
                    section = Section.parse(cell(r, cSection)),
                    weekday = day,
                    orderIndex = cell(r, cOrder).toIntOrNull() ?: (i + 1),
                    title = title,
                    estimate = cell(r, cEstimate),
                )
            )
        }
    }

    private fun parseExercises(rows: List<List<String>>, out: MutableList<Exercise>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cDay = col(h, "día", "dia", "day")
        val cTime = col(h, "hora", "horario", "time")
        val cName = col(h, "ejerci", "movim", "nombre", "exerc")
        val cSets = col(h, "serie", "sets")
        val cReps = col(h, "rep", "repet")
        val cNotes = col(h, "nota", "detalle", "coment")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val day = Weekday.parse(cell(r, cDay))
            val nm = cell(r, cName)
            if (day == null || nm.isBlank()) {
                warn.add("Ejercicios, fila ${i + 2}: falta día o nombre, omitida.")
                return@forEachIndexed
            }
            out.add(
                Exercise(
                    weekday = day,
                    time = normalizeTime(cell(r, cTime)),
                    name = nm,
                    sets = cell(r, cSets).toDoubleOrNull()?.toInt() ?: 0,
                    reps = cell(r, cReps),
                    notes = cell(r, cNotes),
                )
            )
        }
    }

    private fun parseMilestones(rows: List<List<String>>, out: MutableList<Milestone>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cSection = col(h, "context", "secci", "área", "area")
        val cTrack = col(h, "proyect", "track", "meta", "grupo", "sprint")
        val cTitle = col(h, "hito", "fase", "paso", "títul", "titul", "milestone")
        val cStatus = col(h, "estado", "status", "avance")
        val cOrder = col(h, "orden", "order", "#")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val title = cell(r, cTitle)
            val track = cell(r, cTrack)
            if (title.isBlank() || track.isBlank()) {
                warn.add("Hitos, fila ${i + 2}: falta proyecto o hito, omitida.")
                return@forEachIndexed
            }
            out.add(
                Milestone(
                    section = Section.parse(cell(r, cSection)),
                    track = track,
                    orderIndex = cell(r, cOrder).toIntOrNull() ?: (i + 1),
                    title = title,
                    status = MilestoneStatus.parse(cell(r, cStatus)),
                )
            )
        }
    }

    // --- Utilidades ---

    private fun col(header: List<String>, vararg keys: String): Int =
        header.indexOfFirst { cellHead ->
            val hh = cellHead.trim().lowercase()
            keys.any { hh.contains(it) }
        }

    private fun cell(row: List<String>, index: Int): String =
        if (index in row.indices) row[index].trim() else ""

    /** Normaliza "9", "9:00", "0900", "9.30" a "HH:mm"; deja vacío si no aplica. */
    private fun normalizeTime(raw: String): String {
        val t = raw.trim()
        if (t.isBlank()) return ""
        t.toDoubleOrNull()?.let { d ->
            if (d in 0.0..1.0 && t.contains('.')) {
                val totalMin = Math.round(d * 24 * 60).toInt()
                return "%02d:%02d".format(totalMin / 60, totalMin % 60)
            }
        }
        val cleaned = t.replace('.', ':').replace('h', ':')
        val parts = cleaned.split(":", " ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> {
                val hh = parts[0].toIntOrNull() ?: return t
                val mm = parts[1].toIntOrNull() ?: 0
                "%02d:%02d".format(hh % 24, mm % 60)
            }
            parts.size == 1 -> parts[0].toIntOrNull()?.let { "%02d:00".format(it % 24) } ?: t
            else -> t
        }
    }
}
