package com.example.routine_app.data.model

import java.time.DayOfWeek

/** Día de la semana con etiqueta corta en español. Ordinal 1 (Lunes) .. 7 (Domingo). */
enum class Weekday(val iso: Int, val short: String, val label: String) {
    LUNES(1, "Lun", "Lunes"),
    MARTES(2, "Mar", "Martes"),
    MIERCOLES(3, "Mié", "Miércoles"),
    JUEVES(4, "Jue", "Jueves"),
    VIERNES(5, "Vie", "Viernes"),
    SABADO(6, "Sáb", "Sábado"),
    DOMINGO(7, "Dom", "Domingo");

    companion object {
        fun fromIso(iso: Int): Weekday = entries.first { it.iso == iso }
        fun today(): Weekday = fromIso(DayOfWeek.from(java.time.LocalDate.now()).value)

        /** Interpreta texto libre de una plantilla ("lunes", "Lun", "L", "1"). */
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

/** Tipo de meta que se rastrea. */
enum class GoalType(val label: String) {
    CALISTENIA("Calistenia"),
    PROYECTO("Proyecto / Estudio");

    companion object {
        fun parse(raw: String?): GoalType {
            val t = raw?.trim()?.lowercase() ?: return CALISTENIA
            return when {
                t.startsWith("cal") -> CALISTENIA
                t.startsWith("pro") || t.startsWith("est") -> PROYECTO
                else -> CALISTENIA
            }
        }
    }
}

/** Categoría de un bloque de la rutina diaria. */
enum class BlockCategory(val label: String) {
    ESTUDIO("Estudio / Proyecto"),
    EJERCICIO("Ejercicio"),
    PERSONAL("Personal"),
    OTRO("Otro");

    companion object {
        fun parse(raw: String?): BlockCategory {
            val t = raw?.trim()?.lowercase() ?: return OTRO
            return when {
                t.startsWith("est") || t.startsWith("pro") || t.startsWith("stu") -> ESTUDIO
                t.startsWith("eje") || t.startsWith("cal") || t.startsWith("ent") -> EJERCICIO
                t.startsWith("per") -> PERSONAL
                else -> OTRO
            }
        }
    }
}
