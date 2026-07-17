package com.example.routine_app.data.model

import java.time.DayOfWeek
import java.time.LocalDate

/** Día de la semana con etiqueta corta. Ordinal 1 (Lunes) .. 7 (Domingo). */
enum class Weekday(val iso: Int, val letter: String, val short: String, val label: String) {
    LUNES(1, "L", "Lun", "Lunes"),
    MARTES(2, "M", "Mar", "Martes"),
    MIERCOLES(3, "X", "Mié", "Miércoles"),
    JUEVES(4, "J", "Jue", "Jueves"),
    VIERNES(5, "V", "Vie", "Viernes"),
    SABADO(6, "S", "Sáb", "Sábado"),
    DOMINGO(7, "D", "Dom", "Domingo");

    companion object {
        fun fromIso(iso: Int): Weekday = entries.first { it.iso == iso }
        fun today(): Weekday = fromIso(DayOfWeek.from(LocalDate.now()).value)

        fun parse(raw: String?): Weekday? {
            val t = raw?.trim()?.lowercase() ?: return null
            if (t.isEmpty()) return null
            t.toIntOrNull()?.let { n -> if (n in 1..7) return fromIso(n) }
            return when {
                t.startsWith("lu") || t == "l" -> LUNES
                t.startsWith("ma") && !t.startsWith("mi") -> MARTES
                t.startsWith("mi") || t == "x" -> MIERCOLES
                t.startsWith("ju") || t == "j" -> JUEVES
                t.startsWith("vi") || t == "v" -> VIERNES
                t.startsWith("sa") || t == "s" -> SABADO
                t.startsWith("do") || t == "d" -> DOMINGO
                else -> null
            }
        }
    }
}

/** Los dos contextos de nivel superior de la app. */
enum class Section(val label: String) {
    ESTUDIO("Estudio"),
    CALISTENIA("Calistenia");

    companion object {
        fun parse(raw: String?): Section {
            val t = raw?.trim()?.lowercase() ?: return ESTUDIO
            return when {
                t.startsWith("cal") || t.startsWith("ejer") || t.startsWith("entren") -> CALISTENIA
                else -> ESTUDIO
            }
        }
    }
}

/** Etiqueta de un bloque del horario: a qué contexto pertenece (o general). */
enum class ScheduleTag(val label: String) {
    ESTUDIO("Estudio"),
    CALISTENIA("Calistenia"),
    GENERAL("General");

    /** ¿Este bloque es el "foco activo" del contexto dado? */
    fun isFocusOf(section: Section): Boolean = when (this) {
        ESTUDIO -> section == Section.ESTUDIO
        CALISTENIA -> section == Section.CALISTENIA
        GENERAL -> false
    }

    companion object {
        fun parse(raw: String?): ScheduleTag {
            val t = raw?.trim()?.lowercase() ?: return GENERAL
            return when {
                t.startsWith("est") || t.startsWith("pro") || t.startsWith("clas") -> ESTUDIO
                t.startsWith("cal") || t.startsWith("ejer") || t.startsWith("entren") -> CALISTENIA
                else -> GENERAL
            }
        }
    }
}

/** Estado de un hito dentro de un proyecto/track. */
enum class MilestoneStatus {
    DONE,       // ✓ completado
    CURRENT,    // la "Meta" actual
    PENDING;    // pendiente

    /** Avanza al siguiente estado al tocarlo (pending → current → done → pending). */
    fun next(): MilestoneStatus = when (this) {
        PENDING -> CURRENT
        CURRENT -> DONE
        DONE -> PENDING
    }

    companion object {
        fun parse(raw: String?): MilestoneStatus {
            val t = raw?.trim()?.lowercase() ?: return PENDING
            return when {
                t.startsWith("hech") || t.startsWith("done") || t.startsWith("compl") || t == "✓" || t == "x" -> DONE
                t.startsWith("actu") || t.startsWith("curr") || t.startsWith("meta") || t.startsWith("prog") -> CURRENT
                else -> PENDING
            }
        }
    }
}
