# Routine App

App Android (Kotlin + Jetpack Compose) para centralizar en un solo lugar:

- 🗓️ **Rutina diaria** — bloques de estudio/proyecto, ejercicio y personales por día y horario.
- 💪 **Rutina de calistenia** — ejercicios por día de la semana, con series y repeticiones.
- 🎯 **Progreso y metas** — metas de calistenia y de proyecto/estudio, con barra de avance y gráfica de evolución.

Los datos se guardan **localmente en el teléfono** (Room) — sin servidor ni conexión. Se actualizan de dos formas:

1. **Manualmente**, desde la pestaña *Datos* (agregar/editar/eliminar bloques, ejercicios y metas; registrar avances).
2. **Importando una plantilla de Excel** (`.xlsx`) — ver [docs/PLANTILLA.md](docs/PLANTILLA.md).

## Arquitectura

- **UI:** Jetpack Compose + Material 3, navegación con barra inferior (Hoy / Semana / Metas / Datos).
- **Estado:** MVVM — `RoutineViewModel` expone `StateFlow`s.
- **Persistencia:** Room (`AppDatabase`, DAOs, repositorio).
- **Importación:** lector de `.xlsx` propio y ligero (`XlsxReader`, solo `ZipInputStream` + `XmlPullParser`, sin dependencias pesadas) + `TemplateImporter` que mapea hojas/columnas a entidades.
- **Gráficas:** dibujadas con `Canvas` (sin librerías externas).

```
app/src/main/java/com/example/routine_app/
├── data/
│   ├── model/        Entidades y enums (RoutineBlock, Exercise, Goal, ProgressEntry)
│   ├── db/           Room: AppDatabase, DAOs, Converters
│   ├── importer/     XlsxReader + TemplateImporter
│   └── RoutineRepository.kt
└── ui/
    ├── RoutineApp.kt       Navegación + barra inferior
    ├── RoutineViewModel.kt
    ├── components/         LineChart, barras de progreso, etc.
    └── screens/            Today, Week, Goals, Data (+ diálogos)
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
