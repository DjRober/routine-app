# Routine App

App Android (Kotlin + Jetpack Compose) para centralizar en un solo lugar tu **estudio/proyectos**
y tu **calistenia**. Se organiza en **dos contextos** (Estudio ↔ Calistenia) con un switcher tipo
píldora, un selector de día y tres pestañas:

- **Hoy** — foco del día: lista de tareas (Estudio) o de ejercicios (Calistenia), con check.
- **Horario** — línea de tiempo del día; resalta el bloque "activo" según el contexto.
- **Progreso** — checklist de fases/hitos por proyecto (hecho · actual/"Meta" · pendiente).

Incluye **4 temas** (Dark verde/óxido, Matrix, Stealth, Cute) seleccionables desde `◐ Tema`.

Los datos se guardan **localmente en el teléfono** (Room) — sin servidor ni conexión. Se actualizan de dos formas:

1. **Manualmente** — toca cualquier fila para editarla, usa los botones **＋ Agregar**, marca
   tareas/ejercicios y cambia el estado de los hitos. Se guarda automáticamente.
2. **Importando una plantilla de Excel** (`.xlsx`) — ver [docs/PLANTILLA.md](docs/PLANTILLA.md).

## Arquitectura

- **UI:** Jetpack Compose + Material 3. Una sola pantalla con switcher de contexto + pestañas
  (sin barra inferior). Paleta y tipografía por tema vía `LocalAppColors` + `RoutineappTheme`.
- **Estado:** MVVM — `RoutineViewModel` expone `StateFlow`s; tema persistido en `SharedPreferences`.
- **Persistencia:** Room (`AppDatabase`, DAOs, repositorio).
- **Importación:** lector de `.xlsx` propio y ligero (`XlsxReader`, solo `ZipInputStream` + `XmlPullParser`, sin dependencias pesadas) + `TemplateImporter` que mapea hojas/columnas a entidades.

```
app/src/main/java/com/example/routine_app/
├── data/
│   ├── model/        Entidades y enums (ScheduleItem, Task, Exercise, Milestone; Section, ScheduleTag…)
│   ├── db/           Room: AppDatabase, DAOs, Converters
│   ├── importer/     XlsxReader + TemplateImporter
│   └── RoutineRepository.kt
└── ui/
    ├── RoutineApp.kt       Switcher + toolbar + selector de día + pestañas
    ├── RoutineViewModel.kt
    ├── theme/             AppTheme.kt (4 temas)
    ├── components/         Widgets (switcher, day selector, tabs, tool buttons)
    └── screens/            HoyTab, HorarioTab, ProgresoTab + Dialogs
```

## Compilar e instalar en el teléfono

Requisitos: Android Studio (con su JDK) y el SDK de Android.

### Opción A — desde Android Studio
1. Abre el proyecto.
2. Conecta el teléfono por USB con **depuración USB** activada (Ajustes → Opciones de desarrollador).
3. Pulsa **Run ▶**. Se instala y abre la app.

### Opción B — por línea de comandos
```bash
# Genera el APK de depuración
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/app-debug.apk

# Instalar en un teléfono conectado (requiere adb en el PATH)
./gradlew installDebug
```
El APK de depuración es suficiente para uso personal. Para una versión de
lanzamiento firmada, usa **Build → Generate Signed Bundle / APK** en Android Studio.

## Plantilla de Excel

- Archivo listo para usar: [`docs/Plantilla_Rutina.xlsx`](docs/Plantilla_Rutina.xlsx)
- Cómo se estructura y cómo se importa: [`docs/PLANTILLA.md`](docs/PLANTILLA.md)
- Se regenera con: `python tools/generate_template.py`
