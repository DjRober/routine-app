# Plantilla de Excel

La plantilla [`Plantilla_Rutina.xlsx`](Plantilla_Rutina.xlsx) tiene **4 hojas**. Edítala en
Excel (o Google Sheets / LibreOffice) y luego impórtala desde la pestaña **Datos** de la app.

> **Importante:** al importar, los datos actuales de la app se **reemplazan** por completo
> con el contenido de la plantilla. Es la fuente de verdad; edita ahí y vuelve a importar.

El importador es tolerante: reconoce cada hoja y cada columna por el **nombre aproximado**
(en español), sin importar el orden de las columnas. Las filas incompletas se omiten.

## Hoja `Rutina` — rutina diaria

| Columna    | Ejemplo                  | Notas |
|------------|--------------------------|-------|
| Día        | Lunes                    | Lunes…Domingo (también acepta "Lun", "L", "1") |
| Inicio     | 07:00                    | Hora de inicio (opcional) |
| Fin        | 08:00                    | Hora de fin (opcional) |
| Actividad  | Estudio de arquitectura  | Obligatorio |
| Categoría  | Estudio                  | Estudio · Ejercicio · Personal · Otro |
| Notas      | Bloque profundo          | Opcional |

## Hoja `Ejercicios` — calistenia

| Columna   | Ejemplo             | Notas |
|-----------|---------------------|-------|
| Día       | Lunes               | Obligatorio |
| Hora      | 18:00               | Opcional |
| Ejercicio | Flexiones           | Obligatorio |
| Series    | 4                   | Número |
| Reps      | 12-15               | Texto libre (ej: "8-12", "30 seg", "al fallo") |
| Meta      | Flexiones seguidas  | Opcional: enlaza con una fila de la hoja **Metas** por su nombre |
| Notas     |                     | Opcional |

## Hoja `Metas`

| Columna       | Ejemplo             | Notas |
|---------------|---------------------|-------|
| Meta          | Dominadas seguidas  | Obligatorio (este nombre se usa para enlazar) |
| Tipo          | Calistenia          | Calistenia · Proyecto |
| Actual        | 6                   | Valor actual |
| Objetivo      | 15                  | Valor meta |
| Unidad        | reps                | reps · seg · % · horas… |
| Fecha límite  | 2026-12-31          | Formato `yyyy-MM-dd` (opcional) |

## Hoja `Progreso` — evolución de cada meta

| Columna | Ejemplo             | Notas |
|---------|---------------------|-------|
| Meta    | Dominadas seguidas  | Debe coincidir con el nombre en la hoja **Metas** |
| Fecha   | 2026-06-15          | Formato `yyyy-MM-dd` |
| Valor   | 5                   | Número |
| Nota    | Inicio              | Opcional |

Con dos o más registros de progreso de una meta, la app dibuja su **gráfica de evolución**
(pestaña *Metas* → toca la meta para expandirla).

## Consejos

- Los nombres de meta se comparan **sin distinguir mayúsculas ni espacios de sobra**, pero
  escríbelos igual en las hojas *Metas*, *Ejercicios* y *Progreso* para que se enlacen bien.
- Puedes dejar hojas vacías (solo encabezados) si no las usas.
- Las fechas pueden ir como texto `yyyy-MM-dd`, como `dd/mm/aaaa`, o con formato de fecha
  real de Excel — el importador las normaliza.
