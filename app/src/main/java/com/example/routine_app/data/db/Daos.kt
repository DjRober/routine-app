package com.example.routine_app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_items ORDER BY startTime")
    fun observeAll(): Flow<List<ScheduleItem>>

    @Upsert suspend fun upsert(item: ScheduleItem): Long
    @Delete suspend fun delete(item: ScheduleItem)
    @Query("DELETE FROM schedule_items") suspend fun clear()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY orderIndex, id")
    fun observeAll(): Flow<List<Task>>

    @Upsert suspend fun upsert(task: Task): Long
    @Delete suspend fun delete(task: Task)
    @Query("DELETE FROM tasks") suspend fun clear()
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY time, id")
    fun observeAll(): Flow<List<Exercise>>

    @Upsert suspend fun upsert(exercise: Exercise): Long
    @Delete suspend fun delete(exercise: Exercise)
    @Query("DELETE FROM exercises") suspend fun clear()
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones ORDER BY orderIndex, id")
    fun observeAll(): Flow<List<Milestone>>

    @Upsert suspend fun upsert(milestone: Milestone): Long
    @Delete suspend fun delete(milestone: Milestone)
    @Query("DELETE FROM milestones") suspend fun clear()
}
