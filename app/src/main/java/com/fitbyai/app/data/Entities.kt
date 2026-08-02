package com.fitbyai.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.code.gson.Gson
import com.google.code.gson.reflect.TypeToken

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val height: String,
    val age: String,
    val gender: String,
    val goal: String,
    val baseWeight: String,
    val baseWaist: String,
    val experience: String,
    val daysPerWeek: String,
    val equipment: String,
    val limitations: String
)

@Entity(tableName = "workout_tasks")
data class WorkoutTaskEntity(
    @PrimaryKey val taskId: String,
    val exerciseId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val setNumber: Int,
    val totalSets: Int,
    val completed: Boolean
)

@Entity(tableName = "weekly_history")
data class WeeklyHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val week: Int,
    val date: String,
    val weight: Double,
    val waist: Double,
    val sleep: String,
    val energy: Int,
    val rpe: Int,
    val pain: String,
    val completedSets: Int,
    val totalSets: Int,
    val completionRate: Int,
    val feedback: String,
    val exerciseCount: Int
)

@Entity(tableName = "weekly_metadata")
data class WeeklyMetadataEntity(
    @PrimaryKey val id: Int = 1,
    val uploadTimestamp: Long,
    val deadlineTimestamp: Long
)

class StringListConverter {
    @TypeConverter
    fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    toList(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(data, listType) ?: emptyList()
    }
}
