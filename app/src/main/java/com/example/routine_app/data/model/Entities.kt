package com.example.routine_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Bloque del horario diario (timeline). Se muestra en la pestaña Horario. */
@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekday: Weekday,
    val startTime: String,              // "HH:mm"
    val endTime: String = "",           // "HH:mm" (vacío = evento puntual)
    val title: String,
    val tag: ScheduleTag = ScheduleTag.GENERAL,
    val notes: String = "",
)

/** Tarea del día para un contexto (pestaña Hoy de Estudio). */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: Section = Section.ESTUDIO,
    val weekday: Weekday,
    val orderIndex: Int = 0,
    val title: String,
    val estimate: String = "",          // "2h", "45m"…
    val done: Boolean = false,
)

/** Ejercicio de calistenia programado (pestaña Hoy de Calistenia). */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekday: Weekday,
    val time: String = "",              // "HH:mm" (opcional)
    val name: String,
    val sets: Int = 0,
    val reps: String = "",              // "8-12", "30 seg"…
    val done: Boolean = false,
    val notes: String = "",
) {
    /** Detalle compacto "4 × 8-12". */
    val detail: String
        get() = buildString {
            if (sets > 0) append("$sets")
            if (reps.isNotBlank()) { if (isNotEmpty()) append(" × "); append(reps) }
        }
}

/** Hito/fase dentro de un proyecto o meta (pestaña Progreso). */
@Entity(tableName = "milestones")
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: Section = Section.ESTUDIO,
    val track: String,                  // agrupador: "Sprint Diseño", "Dominadas"…
    val orderIndex: Int = 0,
    val title: String,
    val status: MilestoneStatus = MilestoneStatus.PENDING,
)
