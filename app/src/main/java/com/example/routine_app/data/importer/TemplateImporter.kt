package com.example.routine_app.data.importer

import com.example.routine_app.data.model.BlockCategory
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.GoalType
import com.example.routine_app.data.model.ProgressEntry
import com.example.routine_app.data.model.RoutineBlock
import com.example.routine_app.data.model.Weekday
import java.io.InputStream
import java.time.LocalDate

/** Ejercicio recién importado, con el nombre de la meta a la que se debería vincular. */
data class ExerciseImport(val exercise: Exercise, val goalTitleRef: String?)

/** Registro de progreso importado, con el nombre de la meta a la que pertenece. */
data class ProgressImport(val entry: ProgressEntry, val goalTitleRef: String)

/** Contenido completo interpretado desde la plantilla. */
data class ImportedData(
    val blocks: List<RoutineBlock>,
    val exercises: List<ExerciseImport>,
    val goals: List<Goal>,
    val progress: List<ProgressImport>,
) {
    val total: Int get() = blocks.size + exercises.size + goals.size + progress.size
}

/** Resultado de una importación: datos + advertencias legibles. */
data class ImportResult(val data: ImportedData, val warnings: List<String>)

/**
 * Interpreta una plantilla .xlsx con hojas "Rutina", "Ejercicios", "Metas" y "Progreso".
 * Es tolerante: reconoce las hojas y columnas por nombre aproximado (en español),
 * en cualquier orden, y omite filas incompletas acumulando advertencias.
 */
object TemplateImporter {

    fun import(input: InputStream): ImportResult {
        val sheets = XlsxReader.read(input)
        val warnings = ArrayList<String>()

        val blocks = ArrayList<RoutineBlock>()
        val exercises = ArrayList<ExerciseImport>()
        val goals = ArrayList<Goal>()
        val progress = ArrayList<ProgressImport>()

        for ((rawName, rows) in sheets) {
            val name = rawName.trim().lowercase()
            when {
                name.startsWith("rutina") || name.startsWith("dia") || name.startsWith("día") ->
                    parseBlocks(rows, blocks, warnings)
                name.startsWith("ejerci") || name.startsWith("calist") || name.startsWith("entren") ->
                    parseExercises(rows, exercises, warnings)
                name.startsWith("meta") || name.startsWith("objetiv") ->
                    parseGoals(rows, goals, warnings)
                name.startsWith("progres") || name.startsWith("avance") ->
                    parseProgress(rows, progress, warnings)
                else -> warnings.add("Hoja \"$rawName\" ignorada (nombre no reconocido).")
            }
        }
        return ImportResult(ImportedData(blocks, exercises, goals, progress), warnings)
    }

    // --- Hoja: Rutina diaria ---
    private fun parseBlocks(rows: List<List<String>>, out: MutableList<RoutineBlock>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cDay = col(h, "día", "dia", "day")
        val cStart = col(h, "inicio", "desde", "start", "hora")
        val cEnd = col(h, "fin", "hasta", "end")
        val cTitle = col(h, "activ", "títul", "titul", "bloque", "tarea", "nombre")
        val cCat = col(h, "categor", "tipo")
        val cNotes = col(h, "nota", "detalle", "coment")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val day = Weekday.parse(cell(r, cDay))
            val title = cell(r, cTitle)
            if (day == null || title.isBlank()) {
                warn.add("Rutina, fila ${i + 2}: falta día o actividad, omitida.")
                return@forEachIndexed
            }
            out.add(
                RoutineBlock(
                    title = title,
                    weekday = day,
                    startTime = normalizeTime(cell(r, cStart)),
                    endTime = normalizeTime(cell(r, cEnd)),
                    category = BlockCategory.parse(cell(r, cCat)),
                    notes = cell(r, cNotes),
                )
            )
        }
    }

    // --- Hoja: Ejercicios de calistenia ---
    private fun parseExercises(rows: List<List<String>>, out: MutableList<ExerciseImport>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cDay = col(h, "día", "dia", "day")
        val cTime = col(h, "hora", "horario", "time")
        val cName = col(h, "ejerci", "movim", "nombre", "exerc")
        val cSets = col(h, "serie", "sets")
        val cReps = col(h, "rep", "repet")
        val cGoal = col(h, "meta", "objetiv")
        val cNotes = col(h, "nota", "detalle", "coment")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val day = Weekday.parse(cell(r, cDay))
            val nm = cell(r, cName)
            if (day == null || nm.isBlank()) {
                warn.add("Ejercicios, fila ${i + 2}: falta día o nombre, omitida.")
                return@forEachIndexed
            }
            val goalRef = cell(r, cGoal).ifBlank { null }
            out.add(
                ExerciseImport(
                    exercise = Exercise(
                        name = nm,
                        weekday = day,
                        time = normalizeTime(cell(r, cTime)),
                        sets = cell(r, cSets).toDoubleOrNull()?.toInt() ?: 0,
                        reps = cell(r, cReps),
                        notes = cell(r, cNotes),
                    ),
                    goalTitleRef = goalRef,
                )
            )
        }
    }

    // --- Hoja: Metas ---
    private fun parseGoals(rows: List<List<String>>, out: MutableList<Goal>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cTitle = col(h, "meta", "títul", "titul", "objetiv", "nombre")
        val cType = col(h, "tipo", "categor")
        val cTarget = col(h, "objetiv", "target", "meta valor", "valor objet")
        val cCurrent = col(h, "actual", "current", "progreso")
        val cUnit = col(h, "unidad", "unit")
        val cDeadline = col(h, "fecha", "límite", "limite", "plazo", "deadline")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val title = cell(r, cTitle)
            if (title.isBlank()) {
                warn.add("Metas, fila ${i + 2}: sin título, omitida.")
                return@forEachIndexed
            }
            out.add(
                Goal(
                    title = title,
                    type = GoalType.parse(cell(r, cType)),
                    targetValue = cell(r, cTarget).toDoubleOrNull() ?: 0.0,
                    currentValue = cell(r, cCurrent).toDoubleOrNull() ?: 0.0,
                    unit = cell(r, cUnit),
                    deadline = normalizeDate(cell(r, cDeadline)).ifBlank { null },
                )
            )
        }
    }

    // --- Hoja: Progreso ---
    private fun parseProgress(rows: List<List<String>>, out: MutableList<ProgressImport>, warn: MutableList<String>) {
        val h = rows.firstOrNull() ?: return
        val cGoal = col(h, "meta", "objetiv", "nombre")
        val cDate = col(h, "fecha", "date", "día", "dia")
        val cValue = col(h, "valor", "value", "reps", "cantidad")
        val cNote = col(h, "nota", "coment", "detalle")

        rows.drop(1).forEachIndexed { i, r ->
            if (r.all { it.isBlank() }) return@forEachIndexed
            val goalRef = cell(r, cGoal)
            val value = cell(r, cValue).toDoubleOrNull()
            val date = normalizeDate(cell(r, cDate))
            if (goalRef.isBlank() || value == null || date.isBlank()) {
                warn.add("Progreso, fila ${i + 2}: falta meta, fecha o valor, omitida.")
                return@forEachIndexed
            }
            out.add(
                ProgressImport(
                    entry = ProgressEntry(goalId = 0, date = date, value = value, note = cell(r, cNote)),
                    goalTitleRef = goalRef,
                )
            )
        }
    }

    // --- Utilidades ---

    /** Busca el índice de la primera columna cuyo encabezado contenga alguna clave. */
    private fun col(header: List<String>, vararg keys: String): Int =
        header.indexOfFirst { cellHead ->
            val h = cellHead.trim().lowercase()
            keys.any { h.contains(it) }
        }

    private fun cell(row: List<String>, index: Int): String =
        if (index in row.indices) row[index].trim() else ""

    /** Normaliza "9", "9:00", "0900", "9.30" a "HH:mm"; deja vacío si no aplica. */
    private fun normalizeTime(raw: String): String {
        val t = raw.trim()
        if (t.isBlank()) return ""
        // Excel puede guardar la hora como fracción de día (0..1).
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

    /** Normaliza fechas a ISO "yyyy-MM-dd". Acepta ISO, dd/mm/yyyy y seriales de Excel. */
    private fun normalizeDate(raw: String): String {
        val t = raw.trim()
        if (t.isBlank()) return ""
        // Serial de Excel (número de días desde 1899-12-30).
        t.toDoubleOrNull()?.let { serial ->
            if (serial > 59) {
                return runCatching {
                    LocalDate.of(1899, 12, 30).plusDays(serial.toLong()).toString()
                }.getOrDefault(t)
            }
        }
        // ISO ya válido.
        if (Regex("""\d{4}-\d{2}-\d{2}""").matches(t)) return t
        // dd/mm/yyyy o dd-mm-yyyy.
        val m = Regex("""(\d{1,2})[/\-.](\d{1,2})[/\-.](\d{2,4})""").find(t)
        if (m != null) {
            val (d, mo, y) = m.destructured
            val year = if (y.length == 2) "20$y" else y
            return runCatching {
                LocalDate.of(year.toInt(), mo.toInt(), d.toInt()).toString()
            }.getOrDefault(t)
        }
        return t
    }
}
