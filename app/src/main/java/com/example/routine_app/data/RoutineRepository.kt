package com.example.routine_app.data

import android.content.Context
import com.example.routine_app.data.db.AppDatabase
import com.example.routine_app.data.importer.ImportedData
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.ProgressEntry
import com.example.routine_app.data.model.RoutineBlock
import com.example.routine_app.data.model.Weekday
import kotlinx.coroutines.flow.Flow

/** Punto único de acceso a los datos (envuelve los DAOs de Room). */
class RoutineRepository(context: Context) {

    private val db = AppDatabase.get(context)
    private val blockDao = db.routineBlockDao()
    private val exerciseDao = db.exerciseDao()
    private val goalDao = db.goalDao()
    private val progressDao = db.progressEntryDao()

    // --- Observadores ---
    val blocks: Flow<List<RoutineBlock>> = blockDao.observeAll()
    val exercises: Flow<List<Exercise>> = exerciseDao.observeAll()
    val goals: Flow<List<Goal>> = goalDao.observeAll()
    val progress: Flow<List<ProgressEntry>> = progressDao.observeAll()

    fun blocksForDay(day: Weekday) = blockDao.observeForDay(day.name)
    fun exercisesForDay(day: Weekday) = exerciseDao.observeForDay(day.name)
    fun progressForGoal(goalId: Long) = progressDao.observeForGoal(goalId)

    // --- Bloques de rutina ---
    suspend fun saveBlock(block: RoutineBlock) = blockDao.upsert(block)
    suspend fun deleteBlock(block: RoutineBlock) = blockDao.delete(block)

    // --- Ejercicios ---
    suspend fun saveExercise(exercise: Exercise) = exerciseDao.upsert(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    // --- Metas ---
    suspend fun saveGoal(goal: Goal) = goalDao.upsert(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)
    suspend fun findGoalByTitle(title: String) = goalDao.findByTitle(title)

    // --- Progreso ---
    suspend fun saveProgress(entry: ProgressEntry) = progressDao.upsert(entry)
    suspend fun deleteProgress(entry: ProgressEntry) = progressDao.delete(entry)

    /** Reemplaza todo el contenido con el resultado de una importación. */
    suspend fun replaceAll(data: ImportedData) {
        blockDao.clear()
        exerciseDao.clear()
        progressDao.clear()
        goalDao.clear()

        // Inserta metas primero y mapea título -> id para vincular ejercicios/progreso.
        val goalIdByTitle = HashMap<String, Long>()
        for (goal in data.goals) {
            val id = goalDao.upsert(goal.copy(id = 0))
            goalIdByTitle[goal.title.trim().lowercase()] = id
        }
        for (block in data.blocks) blockDao.upsert(block.copy(id = 0))
        for (ex in data.exercises) {
            val linkedId = ex.goalTitleRef?.let { goalIdByTitle[it.trim().lowercase()] }
            exerciseDao.upsert(ex.exercise.copy(id = 0, goalId = linkedId))
        }
        for (p in data.progress) {
            val goalId = goalIdByTitle[p.goalTitleRef.trim().lowercase()]
            if (goalId != null) progressDao.upsert(p.entry.copy(id = 0, goalId = goalId))
        }
    }
}
