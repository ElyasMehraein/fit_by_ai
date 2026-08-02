package com.fitbyai.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // Workout Set Tasks
    @Query("SELECT * FROM workout_tasks")
    fun getTasksFlow(): Flow<List<WorkoutTaskEntity>>

    @Query("SELECT * FROM workout_tasks")
    suspend fun getTasks(): List<WorkoutTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<WorkoutTaskEntity>)

    @Query("UPDATE workout_tasks SET completed = :completed WHERE taskId = :taskId")
    suspend fun updateTaskStatus(taskId: String, completed: Boolean)

    @Query("DELETE FROM workout_tasks")
    suspend fun clearTasks()

    // Weekly History
    @Query("SELECT * FROM weekly_history ORDER BY week ASC")
    fun getHistoryFlow(): Flow<List<WeeklyHistoryEntity>>

    @Query("SELECT * FROM weekly_history ORDER BY week ASC")
    suspend fun getHistory(): List<WeeklyHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: WeeklyHistoryEntity)

    @Query("DELETE FROM weekly_history")
    suspend fun clearHistory()

    // Metadata Timestamps
    @Query("SELECT * FROM weekly_metadata WHERE id = 1 LIMIT 1")
    fun getMetadataFlow(): Flow<WeeklyMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMetadata(metadata: WeeklyMetadataEntity)

    @Query("DELETE FROM weekly_metadata")
    suspend fun clearMetadata()
}
