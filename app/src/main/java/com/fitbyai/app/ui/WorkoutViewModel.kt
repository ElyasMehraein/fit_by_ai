package com.fitbyai.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitbyai.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WorkoutUiState(
    val userProfile: UserProfileEntity? = null,
    val profileHistory: List<ProfileHistoryEntity> = emptyList(),
    val tasks: List<WorkoutTaskEntity> = emptyList(),
    val history: List<WeeklyHistoryEntity> = emptyList(),
    val uploadTimestamp: Long? = null,
    val deadlineTimestamp: Long? = null,
    val selectedTab: String = "queue", // "queue" or "done"
    val generatedPrompt: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.userProfileFlow,
                repository.profileHistoryFlow,
                repository.tasksFlow,
                repository.historyFlow,
                repository.metadataFlow
            ) { profile, profileHistory, tasks, history, metadata ->
                WorkoutUiState(
                    userProfile = profile,
                    profileHistory = profileHistory,
                    tasks = tasks,
                    history = history,
                    uploadTimestamp = metadata?.uploadTimestamp,
                    deadlineTimestamp = metadata?.deadlineTimestamp,
                    selectedTab = _uiState.value.selectedTab,
                    generatedPrompt = _uiState.value.generatedPrompt,
                    errorMessage = _uiState.value.errorMessage,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setSelectedTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleTaskCompletion(taskId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, !currentStatus)
        }
    }

    fun saveProfile(
        height: String, age: String, gender: String, goal: String,
        baseWeight: String, baseWaist: String, experience: String,
        daysPerWeek: String, equipment: String, limitations: String,
        targetWeight: String = "", sessionDuration: String = "",
        activityLevel: String = "", healthConditions: String = ""
    ) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                height = height, age = age, gender = gender, goal = goal,
                baseWeight = baseWeight, baseWaist = baseWaist, experience = experience,
                daysPerWeek = daysPerWeek, equipment = equipment, limitations = limitations,
                targetWeight = targetWeight, sessionDuration = sessionDuration,
                activityLevel = activityLevel, healthConditions = healthConditions
            )
            repository.saveUserProfile(profile)
        }
    }

    fun generatePrompt(
        weight: String, waist: String, sleep: String,
        energy: Int, rpe: Int, pain: String, feedback: String,
        muscleSoreness: String = "نرمال", jointPain: String = "بدون درد مفصلی"
    ) {
        viewModelScope.launch {
            if (weight.isBlank() || waist.isBlank()) {
                _uiState.update { it.copy(errorMessage = "لطفاً فیلدهای وزن و دور کمر را پر کنید.") }
                return@launch
            }
            _uiState.update { it.copy(errorMessage = null) }
            val prompt = repository.generateAiPrompt(
                weight, waist, sleep, energy, rpe, pain, feedback, muscleSoreness, jointPain
            )
            _uiState.update { it.copy(generatedPrompt = prompt) }
        }
    }

    fun importProgram(
        jsonRaw: String, weight: String, waist: String,
        sleep: String, energy: Int, rpe: Int, pain: String, feedback: String,
        muscleSoreness: String = "نرمال", jointPain: String = "بدون درد مفصلی",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.importJsonProgram(
                jsonRaw, weight, waist, sleep, energy, rpe, pain, feedback, muscleSoreness, jointPain
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, errorMessage = null, generatedPrompt = null) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}

class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
