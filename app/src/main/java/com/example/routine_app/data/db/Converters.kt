package com.example.routine_app.data.db

import androidx.room.TypeConverter
import com.example.routine_app.data.model.MilestoneStatus
import com.example.routine_app.data.model.ScheduleTag
import com.example.routine_app.data.model.Section
import com.example.routine_app.data.model.Weekday

/** Convierte los enums a/desde su nombre para almacenarlos en Room. */
class Converters {
    @TypeConverter fun weekdayToString(v: Weekday): String = v.name
    @TypeConverter fun stringToWeekday(v: String): Weekday = Weekday.valueOf(v)

    @TypeConverter fun sectionToString(v: Section): String = v.name
    @TypeConverter fun stringToSection(v: String): Section = Section.valueOf(v)

    @TypeConverter fun tagToString(v: ScheduleTag): String = v.name
    @TypeConverter fun stringToTag(v: String): ScheduleTag = ScheduleTag.valueOf(v)

    @TypeConverter fun statusToString(v: MilestoneStatus): String = v.name
    @TypeConverter fun stringToStatus(v: String): MilestoneStatus = MilestoneStatus.valueOf(v)
}
