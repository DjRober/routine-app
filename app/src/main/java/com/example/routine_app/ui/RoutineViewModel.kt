package com.example.routine_app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routine_app.data.RoutineRepository
import com.example.routine_app.data.importer.ImportResult
import com.example.routine_app.data.importer.TemplateImporter
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.ProgressEntry
import com.example.routine_app.data.model.RoutineBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estado del último intento de importación, para mostrarlo en la UI. */
sealed interface ImportState {
    data object Idle : ImportState
    data object Loading : ImportState
    data class Success(val result: ImportResult) : ImportState
    data class Error(val message: String) : ImportState
}

class RoutineViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RoutineRepository(app)

    val blocks: StateFlow<List<RoutineBlock>> = repo.blocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val exercises: StateFlow<List<Exercise>> = repo.exercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals: StateFlow<List<Goal>> = repo.goals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val progress: StateFlow<List<ProgressEntry>> = repo.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var _importState: ImportState = ImportState.Idle
    val importState get() = _importState

    // --- Mutaciones (CRUD manual) ---
    fun saveBlock(block: RoutineBlock) = viewModelScope.launch { repo.saveBlock(block) }
    fun deleteBlock(block: RoutineBlock) = viewModelScope.launch { repo.deleteBlock(block) }

    fun saveExercise(exercise: Exercise) = viewModelScope.launch { repo.saveExercise(exercise) }
    fun deleteExercise(exercise: Exercise) = viewModelScope.launch { repo.deleteExercise(exercise) }

    fun saveGoal(goal: Goal) = viewModelScope.launch { repo.saveGoal(goal) }
    fun deleteGoal(goal: Goal) = viewModelScope.launch { repo.deleteGoal(goal) }

    fun saveProgress(entry: ProgressEntry) = viewModelScope.launch { repo.saveProgress(entry) }
    fun deleteProgress(entry: ProgressEntry) = viewModelScope.launch { repo.deleteProgress(entry) }

    /** Importa una plantilla .xlsx desde el archivo elegido por el usuario. */
    fun importFromUri(uri: Uri, onFinished: (ImportState) -> Unit) {
        viewModelScope.launch {
            onFinished(ImportState.Loading)
            val state = try {
                val result = withContext(Dispatchers.IO) {
                    val resolver = getApplication<Application>().contentResolver
                    val stream = resolver.openInputStream(uri)
                        ?: error("No se pudo abrir el archivo.")
                    stream.use { TemplateImporter.import(it) }
                }
                repo.replaceAll(result.data)
                ImportState.Success(result)
            } catch (e: Exception) {
                ImportState.Error(e.message ?: "Error desconocido al importar.")
            }
            _importState = state
            onFinished(state)
        }
    }
}
