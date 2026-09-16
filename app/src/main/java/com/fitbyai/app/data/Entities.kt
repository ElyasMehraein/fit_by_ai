package com.fitbyai.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    val limitations: String,
    val targetWeight: String = "",
    val sessionDuration: String = "",
    val activityLevel: String = "",
    val healthConditions: String = ""
)

@Entity(tableName = "profile_history")
data class ProfileHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val height: String,
    val age: String,
    val gender: String,
    val goal: String,
    val baseWeight: String,
    val baseWaist: String,
    val experience: String,
    val daysPerWeek: String,
    val equipment: String,
    val limitations: String,
    val targetWeight: String = "",
    val sessionDuration: String = "",
    val activityLevel: String = "",
    val healthConditions: String = ""
)

fun getRelativeTimeSpanString(timestamp: Long, strings: com.fitbyai.app.i18n.AppStrings? = null): String {
    val now = System.currentTimeMillis()
    val diffMs = now - timestamp
    if (diffMs < 0) return strings?.timeJustNow ?: "Just now"
    val diffMinutes = diffMs / (1000 * 60)
    val diffHours = diffMs / (1000 * 60 * 60)
    val diffDays = diffMs / (1000 * 60 * 60 * 24)

    return when {
        diffMinutes < 2 -> strings?.timeJustNow ?: "Just now"
        diffMinutes < 60 -> strings?.timeMinutesAgo(diffMinutes) ?: "$diffMinutes min ago"
        diffHours < 24 -> strings?.timeHoursAgo(diffHours) ?: "$diffHours hr ago"
        diffDays == 1L -> strings?.timeYesterday ?: "Yesterday"
        diffDays < 30 -> strings?.timeDaysAgo(diffDays) ?: "$diffDays days ago"
        diffDays < 365 -> strings?.timeMonthsAgo(diffDays / 30) ?: "${diffDays / 30} months ago"
        else -> strings?.timeYearsAgo(diffDays / 365) ?: "${diffDays / 365} years ago"
    }
}

@Entity(tableName = "workout_tasks")
data class WorkoutTaskEntity(
    @PrimaryKey val taskId: String,
    val exerciseId: String,
    val title: String,
    val description: String,
    val targetPerSet: String = "",
    val images: List<String>,
    val setNumber: Int,
    val totalSets: Int,
    val completed: Boolean,
    val movementPattern: String = "",
    val targetMuscle: String = "",
    val day: Int = 1
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
    val exerciseCount: Int,
    val exerciseSummary: String = "",
    val muscleSoreness: String = "",
    val jointPain: String = ""
)

@Entity(tableName = "weekly_metadata")
data class WeeklyMetadataEntity(
    @PrimaryKey val id: Int = 1,
    val uploadTimestamp: Long,
    val deadlineTimestamp: Long
)

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(data, listType) ?: emptyList()
    }
}
