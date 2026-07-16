package com.example.routine_app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Goal
import com.example.routine_app.data.model.ProgressEntry
import com.example.routine_app.data.model.RoutineBlock
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineBlockDao {
    @Query("SELECT * FROM routine_blocks ORDER BY startTime")
    fun observeAll(): Flow<List<RoutineBlock>>

    @Query("SELECT * FROM routine_blocks WHERE weekday = :weekday ORDER BY startTime")
    fun observeForDay(weekday: String): Flow<List<RoutineBlock>>

    @Upsert
    suspend fun upsert(block: RoutineBlock): Long

    @Delete
    suspend fun delete(block: RoutineBlock)

    @Query("DELETE FROM routine_blocks")
    suspend fun clear()
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY time")
    fun observeAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE weekday = :weekday ORDER BY time")
    fun observeForDay(weekday: String): Flow<List<Exercise>>

    @Upsert
    suspend fun upsert(exercise: Exercise): Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises")
    suspend fun clear()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY type, title")
    fun observeAll(): Flow<List<Goal>>

    @Upsert
    suspend fun upsert(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("SELECT * FROM goals WHERE title = :title LIMIT 1")
    suspend fun findByTitle(title: String): Goal?

    @Query("DELETE FROM goals")
    suspend fun clear()
}

@Dao
interface ProgressEntryDao {
    @Query("SELECT * FROM progress_entries ORDER BY date")
    fun observeAll(): Flow<List<ProgressEntry>>

    @Query("SELECT * FROM progress_entries WHERE goalId = :goalId ORDER BY date")
    fun observeForGoal(goalId: Long): Flow<List<ProgressEntry>>

    @Upsert
    suspend fun upsert(entry: ProgressEntry): Long

    @Delete
    suspend fun delete(entry: ProgressEntry)

    @Query("DELETE FROM progress_entries")
    suspend fun clear()
}
