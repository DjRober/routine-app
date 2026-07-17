package com.example.routine_app.data

import android.content.Context
import com.example.routine_app.data.db.AppDatabase
import com.example.routine_app.data.importer.ImportedData
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.Task
import kotlinx.coroutines.flow.Flow

/** Punto único de acceso a los datos (envuelve los DAOs de Room). */
class RoutineRepository(context: Context) {

    private val db = AppDatabase.get(context)
    private val scheduleDao = db.scheduleDao()
    private val taskDao = db.taskDao()
    private val exerciseDao = db.exerciseDao()
    private val milestoneDao = db.milestoneDao()

    val schedule: Flow<List<ScheduleItem>> = scheduleDao.observeAll()
    val tasks: Flow<List<Task>> = taskDao.observeAll()
    val exercises: Flow<List<Exercise>> = exerciseDao.observeAll()
    val milestones: Flow<List<Milestone>> = milestoneDao.observeAll()

    suspend fun saveSchedule(item: ScheduleItem) = scheduleDao.upsert(item)
    suspend fun deleteSchedule(item: ScheduleItem) = scheduleDao.delete(item)

    suspend fun saveTask(task: Task) = taskDao.upsert(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)

    suspend fun saveExercise(exercise: Exercise) = exerciseDao.upsert(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    suspend fun saveMilestone(milestone: Milestone) = milestoneDao.upsert(milestone)
    suspend fun deleteMilestone(milestone: Milestone) = milestoneDao.delete(milestone)

    /** Reemplaza todo el contenido con el resultado de una importación. */
    suspend fun replaceAll(data: ImportedData) {
        scheduleDao.clear()
        taskDao.clear()
        exerciseDao.clear()
        milestoneDao.clear()
        data.schedule.forEach { scheduleDao.upsert(it.copy(id = 0)) }
        data.tasks.forEach { taskDao.upsert(it.copy(id = 0)) }
        data.exercises.forEach { exerciseDao.upsert(it.copy(id = 0)) }
        data.milestones.forEach { milestoneDao.upsert(it.copy(id = 0)) }
    }
}
