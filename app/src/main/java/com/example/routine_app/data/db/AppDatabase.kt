package com.example.routine_app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.routine_app.data.model.Exercise
import com.example.routine_app.data.model.Milestone
import com.example.routine_app.data.model.ScheduleItem
import com.example.routine_app.data.model.Task

@Database(
    entities = [ScheduleItem::class, Task::class, Exercise::class, Milestone::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun milestoneDao(): MilestoneDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "routine.db",
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
    }
}
