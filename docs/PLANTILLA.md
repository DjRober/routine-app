# Plantilla de Excel

La plantilla [`Plantilla_Rutina.xlsx`](Plantilla_Rutina.xlsx) tiene **4 hojas**. Edítala en
Excel (o Google Sheets / LibreOffice) y luego impórtala desde el botón **⬒ Importar Excel**
de la app.

> **Importante:** al importar, los datos actuales de la app se **reemplazan** por completo
> con el contenido de la plantilla. Es la fuente de verdad; edita ahí y vuelve a importar.

El importador es tolerante: reconoce cada hoja y cada columna por el **nombre aproximado**
(en español), sin importar el orden de las columnas. Las filas incompletas se omiten.

## Contextos

La app se organiza en dos contextos: **Estudio** y **Calistenia**. Varias hojas tienen una
columna **Contexto** para indicar a cuál pertenece cada fila.

## Hoja `Horario` — línea de tiempo del día

Se muestra en la pestaña **Horario**. El bloque cuyo *Contexto* coincide con el contexto
activo se resalta como "foco activo".

| Columna    | Ejemplo        | Notas |
|------------|----------------|-------|
| Día        | Jueves         | Lunes…Domingo (también "Jue", "J", "4") |
| Inicio     | 07:00          | Hora de inicio |
| Fin        | 14:00          | Hora de fin (opcional; vacío = evento puntual) |
| Actividad  | Clases         | Obligatorio |
| Contexto   | Estudio        | Estudio · Calistenia · General |
| Notas      | Foco mañana    | Opcional |

## Hoja `Tareas` — pestaña Hoy (Estudio)

| Columna    | Ejemplo              | Notas |
|------------|----------------------|-------|
| Contexto   | Estudio              | Estudio · Calistenia |
| Día        | Jueves               | Obligatorio |
| Tarea      | Wireframes pantallas | Obligatorio |
| Estimación | 2h                   | Texto libre ("2h", "45m") |
| Orden      | 1                    | Número para ordenar (opcional) |

## Hoja `Ejercicios` — pestaña Hoy (Calistenia)

| Columna   | Ejemplo    | Notas |
|-----------|------------|-------|
| Día       | Jueves     | Obligatorio |
| Hora      | 16:30      | Opcional |
| Ejercicio | Flexiones  | Obligatorio |
| Series    | 4          | Número |
| Reps      | 12-15      | Texto libre ("8-12", "30 seg") |
| Notas     |            | Opcional |

## Hoja `Hitos` — pestaña Progreso

Cada proyecto (columna **Proyecto**) agrupa sus hitos como un checklist. El hito en estado
*Actual* se marca como "Meta".

| Columna   | Ejemplo               | Notas |
|-----------|-----------------------|-------|
| Contexto  | Estudio               | Estudio · Calistenia |
| Proyecto  | Sprint Diseño         | Agrupador del checklist |
| Hito      | Prototipo funcional   | Obligatorio |
| Estado    | Actual                | Hecho · Actual · Pendiente |
| Orden     | 3                     | Número para ordenar (opcional) |

## Consejos

- Puedes dejar hojas vacías (solo encabezados) si no las usas.
- En la app, todo se puede editar también a mano: toca cualquier fila para editarla, usa
  los botones **＋ Agregar**, y en Progreso toca el cuadro de un hito para cambiar su estado
  (pendiente → actual → hecho). Se guarda automáticamente.
