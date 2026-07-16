package com.example.routine_app.data.db

import androidx.room.TypeConverter
import com.example.routine_app.data.model.BlockCategory
import com.example.routine_app.data.model.GoalType
import com.example.routine_app.data.model.Weekday

/** Convierte los enums a/desde su nombre para almacenarlos en Room. */
class Converters {
    @TypeConverter fun weekdayToString(v: Weekday): String = v.name
    @TypeConverter fun stringToWeekday(v: String): Weekday = Weekday.valueOf(v)

    @TypeConverter fun goalTypeToString(v: GoalType): String = v.name
    @TypeConverter fun stringToGoalType(v: String): GoalType = GoalType.valueOf(v)

    @TypeConverter fun blockCategoryToString(v: BlockCategory): String = v.name
    @TypeConverter fun stringToBlockCategory(v: String): BlockCategory = BlockCategory.valueOf(v)
}
