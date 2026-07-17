package com.example.routine_app.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routine_app.data.RoutineRepository
import com.example.routine_app.data.importer.ImportResult
import com.example.routine_app.data.importer.TemplateImporter
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.Task
import com.example.routine_app.ui.theme.ThemeId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estado del último intento de importación. */
sealed interface ImportState {
    data object Idle : ImportState
    data object Loading : ImportState
    data class Success(val result: ImportResult) : ImportState
    data class Error(val message: String) : ImportState
}

class RoutineViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RoutineRepository(app)
    private val prefs = app.getSharedPreferences("routine_prefs", Context.MODE_PRIVATE)

    private val _themeId = MutableStateFlow(ThemeId.from(prefs.getString("theme", null)))
    val themeId: StateFlow<ThemeId> = _themeId.asStateFlow()

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    val schedule: StateFlow<List<ScheduleItem>> = repo.schedule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasks: StateFlow<List<Task>> = repo.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val exercises: StateFlow<List<Exercise>> = repo.exercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val milestones: StateFlow<List<Milestone>> = repo.milestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTheme(id: ThemeId) {
        _themeId.value = id
        prefs.edit().putString("theme", id.name).apply()
    }

    // --- Horario ---
    fun saveSchedule(item: ScheduleItem) = viewModelScope.launch { repo.saveSchedule(item) }
    fun deleteSchedule(item: ScheduleItem) = viewModelScope.launch { repo.deleteSchedule(item) }

    // --- Tareas ---
    fun saveTask(task: Task) = viewModelScope.launch { repo.saveTask(task) }
    fun deleteTask(task: Task) = viewModelScope.launch { repo.deleteTask(task) }
    fun toggleTask(task: Task) = viewModelScope.launch { repo.saveTask(task.copy(done = !task.done)) }

    // --- Ejercicios ---
    fun saveExercise(exercise: Exercise) = viewModelScope.launch { repo.saveExercise(exercise) }
    fun deleteExercise(exercise: Exercise) = viewModelScope.launch { repo.deleteExercise(exercise) }
    fun toggleExercise(exercise: Exercise) =
        viewModelScope.launch { repo.saveExercise(exercise.copy(done = !exercise.done)) }

    // --- Hitos ---
    fun saveMilestone(m: Milestone) = viewModelScope.launch { repo.saveMilestone(m) }
    fun deleteMilestone(m: Milestone) = viewModelScope.launch { repo.deleteMilestone(m) }
    fun cycleMilestone(m: Milestone) =
        viewModelScope.launch { repo.saveMilestone(m.copy(status = m.status.next())) }

    /** Importa una plantilla .xlsx desde el archivo elegido por el usuario. */
    fun importFromUri(uri: Uri) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            _importState.value = try {
                val result = withContext(Dispatchers.IO) {
                    val resolver = getApplication<Application>().contentResolver
                    val stream = resolver.openInputStream(uri) ?: error("No se pudo abrir el archivo.")
                    stream.use { TemplateImporter.import(it) }
                }
                repo.replaceAll(result.data)
                ImportState.Success(result)
            } catch (e: Exception) {
                ImportState.Error(e.message ?: "Error desconocido al importar.")
            }
        }
    }

    fun dismissImportState() { _importState.value = ImportState.Idle }
}
