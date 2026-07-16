package com.example.routine_app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Bloque de la rutina diaria (estudio, ejercicio, personal…). */
@Entity(tableName = "routine_blocks")
data class RoutineBlock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val weekday: Weekday,
    val startTime: String,          // "HH:mm"
    val endTime: String,            // "HH:mm"
    val category: BlockCategory = BlockCategory.OTRO,
    val notes: String = "",
)

/** Ejercicio de calistenia programado en un día/horario. */
@Entity(
    tableName = "exercises",
    indices = [Index("goalId")],
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val weekday: Weekday,
    val time: String = "",          // "HH:mm" (opcional)
    val sets: Int = 0,
    val reps: String = "",          // libre: "8-12", "30 seg", "al fallo"
    val goalId: Long? = null,       // meta de calistenia vinculada (opcional)
    val notes: String = "",
)

/** Meta rastreable (calistenia o proyecto/estudio). */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: GoalType = GoalType.CALISTENIA,
    val targetValue: Double = 0.0,
    val currentValue: Double = 0.0,
    val unit: String = "",          // "reps", "seg", "%", "kg"…
    val deadline: String? = null,   // ISO "yyyy-MM-dd" (opcional)
) {
    /** Avance 0f..1f respecto al objetivo. */
    val progressFraction: Float
        get() = if (targetValue <= 0.0) 0f
        else (currentValue / targetValue).coerceIn(0.0, 1.0).toFloat()
}

/** Registro fechado de progreso de una meta (alimenta las gráficas). */
@Entity(
    tableName = "progress_entries",
    indices = [Index("goalId")],
)
data class ProgressEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val date: String,               // ISO "yyyy-MM-dd"
    val value: Double,
    val note: String = "",
)
