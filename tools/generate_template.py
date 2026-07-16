"""Genera la plantilla de Excel para importar en Routine App.

Uso:  python tools/generate_template.py
Produce: docs/Plantilla_Rutina.xlsx

Las hojas y los encabezados coinciden con lo que reconoce el importador
(TemplateImporter.kt). Puedes reordenar columnas o renombrar cerca del original;
el importador identifica cada campo por palabras clave.
"""
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.worksheet.datavalidation import DataValidation
import os

HEADER_FILL = PatternFill("solid", fgColor="1F6FEB")
HEADER_FONT = Font(bold=True, color="FFFFFF")
THIN = Side(style="thin", color="D0D7DE")
BORDER = Border(left=THIN, right=THIN, top=THIN, bottom=THIN)

DIAS = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
CATEGORIAS = ["Estudio", "Ejercicio", "Personal", "Otro"]
TIPOS = ["Calistenia", "Proyecto"]


def style_headers(ws, ncols):
    for c in range(1, ncols + 1):
        cell = ws.cell(row=1, column=c)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = Alignment(horizontal="center", vertical="center")
        cell.border = BORDER
    ws.row_dimensions[1].height = 22
    ws.freeze_panes = "A2"


def autosize(ws, widths):
    for i, w in enumerate(widths, start=1):
        ws.column_dimensions[chr(64 + i)].width = w


def add_list_validation(ws, options, col_letter, last_row=200):
    dv = DataValidation(type="list", formula1='"%s"' % ",".join(options), allow_blank=True)
    ws.add_data_validation(dv)
    dv.add(f"{col_letter}2:{col_letter}{last_row}")


wb = Workbook()

# --- Hoja 1: Rutina diaria ---
ws = wb.active
ws.title = "Rutina"
ws.append(["Día", "Inicio", "Fin", "Actividad", "Categoría", "Notas"])
ejemplos_rutina = [
    ["Lunes", "07:00", "08:00", "Estudio de arquitectura", "Estudio", "Bloque profundo"],
    ["Lunes", "18:00", "19:00", "Calistenia - Empuje", "Ejercicio", ""],
    ["Martes", "07:00", "08:00", "Proyecto personal", "Estudio", ""],
    ["Miércoles", "18:00", "19:00", "Calistenia - Tracción", "Ejercicio", ""],
]
for r in ejemplos_rutina:
    ws.append(r)
style_headers(ws, 6)
autosize(ws, [12, 10, 10, 28, 14, 24])
add_list_validation(ws, DIAS, "A")
add_list_validation(ws, CATEGORIAS, "E")

# --- Hoja 2: Ejercicios de calistenia ---
ws = wb.create_sheet("Ejercicios")
ws.append(["Día", "Hora", "Ejercicio", "Series", "Reps", "Meta", "Notas"])
ejemplos_ex = [
    ["Lunes", "18:00", "Flexiones", 4, "12-15", "Flexiones seguidas", ""],
    ["Lunes", "18:20", "Fondos en paralelas", 4, "8-10", "", ""],
    ["Miércoles", "18:00", "Dominadas", 5, "6-8", "Dominadas seguidas", ""],
    ["Miércoles", "18:25", "Remo australiano", 4, "12", "", ""],
    ["Viernes", "18:00", "Plancha (hold)", 3, "30 seg", "Plancha 60s", ""],
]
for r in ejemplos_ex:
    ws.append(r)
style_headers(ws, 7)
autosize(ws, [12, 8, 24, 8, 12, 22, 20])
add_list_validation(ws, DIAS, "A")

# --- Hoja 3: Metas ---
ws = wb.create_sheet("Metas")
ws.append(["Meta", "Tipo", "Actual", "Objetivo", "Unidad", "Fecha límite"])
ejemplos_metas = [
    ["Dominadas seguidas", "Calistenia", 6, 15, "reps", "2026-12-31"],
    ["Flexiones seguidas", "Calistenia", 20, 50, "reps", "2026-12-31"],
    ["Plancha 60s", "Calistenia", 30, 60, "seg", "2026-10-31"],
    ["Avance del proyecto", "Proyecto", 35, 100, "%", "2026-09-30"],
    ["Horas de estudio (mes)", "Proyecto", 12, 40, "horas", ""],
]
for r in ejemplos_metas:
    ws.append(r)
style_headers(ws, 6)
autosize(ws, [26, 14, 10, 10, 10, 14])
add_list_validation(ws, TIPOS, "B")
# Fecha límite como texto ISO para lectura inequívoca.
for row in range(2, 60):
    ws.cell(row=row, column=6).number_format = "@"

# --- Hoja 4: Progreso ---
ws = wb.create_sheet("Progreso")
ws.append(["Meta", "Fecha", "Valor", "Nota"])
ejemplos_prog = [
    ["Dominadas seguidas", "2026-06-01", 4, "Inicio"],
    ["Dominadas seguidas", "2026-06-15", 5, ""],
    ["Dominadas seguidas", "2026-07-01", 6, ""],
    ["Avance del proyecto", "2026-06-01", 20, ""],
    ["Avance del proyecto", "2026-07-01", 35, "Módulo de datos listo"],
]
for r in ejemplos_prog:
    ws.append(r)
style_headers(ws, 4)
autosize(ws, [26, 14, 10, 28])
for row in range(2, 60):
    ws.cell(row=row, column=2).number_format = "@"

out_dir = os.path.join(os.path.dirname(__file__), "..", "docs")
os.makedirs(out_dir, exist_ok=True)
out_path = os.path.abspath(os.path.join(out_dir, "Plantilla_Rutina.xlsx"))
wb.save(out_path)
print("Plantilla generada en:", out_path)
