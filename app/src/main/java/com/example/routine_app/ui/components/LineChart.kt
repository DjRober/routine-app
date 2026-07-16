package com.example.routine_app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Gráfica de líneas simple dibujada con Canvas (sin dependencias externas).
 * [values] son los puntos en orden cronológico.
 */
@Composable
fun LineChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
    target: Float? = null,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            if (values.isEmpty()) return@Canvas
            val padding = 12.dp.toPx()
            val w = size.width - padding * 2
            val h = size.height - padding * 2

            val maxV = (target?.let { maxOf(it, values.max()) } ?: values.max()).coerceAtLeast(1f)
            val minV = minOf(0f, values.min())
            val range = (maxV - minV).coerceAtLeast(1f)

            fun x(i: Int): Float =
                if (values.size == 1) padding + w / 2
                else padding + w * i / (values.size - 1)

            fun y(v: Float): Float = padding + h * (1f - (v - minV) / range)

            // Líneas de referencia horizontales.
            for (g in 0..4) {
                val gy = padding + h * g / 4
                drawLine(gridColor.copy(alpha = 0.4f), Offset(padding, gy), Offset(padding + w, gy), 1f)
            }

            // Línea del objetivo.
            target?.let {
                val ty = y(it)
                drawLine(
                    color = lineColor.copy(alpha = 0.5f),
                    start = Offset(padding, ty),
                    end = Offset(padding + w, ty),
                    strokeWidth = 2f,
                )
            }

            // Área bajo la curva.
            val fill = Path().apply {
                moveTo(x(0), y(values[0]))
                values.forEachIndexed { i, v -> lineTo(x(i), y(v)) }
                lineTo(x(values.lastIndex), padding + h)
                lineTo(x(0), padding + h)
                close()
            }
            drawPath(fill, fillColor)

            // Línea principal.
            val line = Path().apply {
                moveTo(x(0), y(values[0]))
                values.forEachIndexed { i, v -> lineTo(x(i), y(v)) }
            }
            drawPath(line, lineColor, style = Stroke(width = 3f))

            // Puntos.
            values.forEachIndexed { i, v ->
                drawCircle(lineColor, radius = 4f, center = Offset(x(i), y(v)))
            }
        }
    }
}
