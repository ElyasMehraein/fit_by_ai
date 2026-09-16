package com.fitbyai.app.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // TopBar & General
    val appName: String
    val appSubtitle: String
    val selectLanguage: String

    // Bottom Navigation
    val navWorkouts: String
    val navAiAssistant: String
    val navManualProgram: String
    val navProfile: String

    // Day Toggle
    val allWeek: String
    fun dayN(day: Int): String

    // Tabs
    fun inQueue(count: Int): String
    fun completed(count: Int): String

    // Hero Progress Card
    fun weekN(week: Int): String
    val activeWeekBadge: String
    fun setsProgress(done: Int, total: Int): String
    val weeklyWorkoutProgress: String
    fun weekCompletedCelebration(week: Int): String
    val timeRemainingFreeText: String
    val endOfWeekPromptNotice: String
    val getNewWeekPlanBtn: String
    fun deadlineRemaining(days: Long, hours: Long): String
    fun deadlineHoursOnly(hours: Long): String
    val deadlineEnded: String

    // Muscle Volume
    val muscleDistributionTitle: String
    val scientificVolumeBadge: String
    fun setsCount(sets: Int): String
    val muscleChest: String
    val muscleBack: String
    val muscleLegs: String
    val muscleShoulders: String
    val muscleArms: String
    val muscleAbs: String
    val muscleOther: String
    fun localizedMuscle(name: String): String

    // Exercise Cards
    fun setProgressLabel(setNumber: Int, totalSets: Int): String
    fun queuedBadge(setNumber: Int, totalSets: Int): String
    fun remainingSetsBadge(count: Int): String
    val targetLabel: String
    val musclePrefix: String
    val defaultTarget: String
    val markComplete: String
    val markIncomplete: String
    val changeImage: String
    val searchGoogleImages: String
    val changeGoogleImage: String
    val deleteSet: String
    fun completeSetBtn(setNumber: Int, isLast: Boolean, isDone: Boolean): String
    val restoreToQueue: String
    val noQueueTasks: String
    val noDoneTasks: String

    // Profile Dialog
    val profileTitle: String
    val tabSpecs: String
    val tabHistory: String
    val tabSettings: String
    val profileIntroNotice: String
    val genderLabel: String
    val genderMale: String
    val genderFemale: String
    val heightLabel: String
    val ageLabel: String
    val goalLabel: String
    val baseWeightLabel: String
    val baseWaistLabel: String
    val targetWeightLabel: String
    val experienceLabel: String
    val experienceYearsSuffix: String
    val daysPerWeekLabel: String
    val daysSuffix: String
    val sessionDurationLabel: String
    val minutesSuffix: String
    val activityLevelLabel: String
    val activitySedentary: String
    val activityModerate: String
    val activityActive: String
    val equipmentLabel: String
    val limitationsLabel: String
    val healthConditionsLabel: String
    val saveProfileBtn: String
    val languageSectionTitle: String
    val languageSectionDesc: String
    val resetDataTitle: String
    val resetDataDesc: String
    val resetDataBtn: String
    val resetConfirmTitle: String
    val resetConfirmMsg: String
    val confirmResetBtn: String
    val cancelBtn: String

    // Weekly Review Dialog
    val weeklyReviewTitle: String
    val reviewIntroNotice: String
    fun profileLastUpdated(timeStr: String): String
    val editProfileBtn: String
    val newWeightLabel: String
    val newWaistLabel: String
    val sleepLabel: String
    val energyLabel: String
    val rpeLabel: String
    val muscleSorenessLabel: String
    val sorenessNormal: String
    val sorenessHigh: String
    val sorenessExtreme: String
    val jointPainLabel: String
    val jointPainNone: String
    val jointPainMild: String
    val feedbackLabel: String
    val generatePromptBtn: String
    val generatedPromptTitle: String
    val copyPromptBtn: String
    val promptCopiedToast: String
    val promptInstructions: String
    val importProgramTitle: String
    val jsonPlaceholder: String
    val importProgramBtn: String
    val loadingText: String
    val errorWeightWaistRequired: String

    // Manual Program Dialog
    val manualProgramTitle: String
    val manualProgramDesc: String
    val saveManualProgramBtn: String

    // History Dialog
    val historyTitle: String
    fun historyWeekTitle(week: Int, date: String): String
    fun historyWeightWaist(weight: Double, waist: Double): String
    fun historyRecovery(sleep: String, energy: Int, rpe: Int, soreness: String, joint: String): String
    fun historySetsCompleted(done: Int, total: Int, rate: Int): String
    fun historyFeedback(feedback: String): String
    val emptyHistoryNotice: String

    // Empty States
    val noProfileTitle: String
    val noProfileDesc: String
    val openProfileBtn: String
    val noProgramTitle: String
    val noProgramDesc: String
    val getStartedBtn: String

    // Image Search Dialog
    val searchImageTitle: String
    val searchImageHint: String
    val searchBtn: String
    val noImagesFound: String
    val closeBtn: String
    val searchImageInstruction: String
    val imageSelectedReady: String
    fun imageReplaceNotice(exerciseTitle: String): String
    val pickAnotherImage: String
    val saveAsExerciseImage: String
    val exerciseSearchQueryPrefix: String

    // Relative Time
    val timeJustNow: String
    fun timeMinutesAgo(minutes: Long): String
    fun timeHoursAgo(hours: Long): String
    val timeYesterday: String
    fun timeDaysAgo(days: Long): String
    fun timeMonthsAgo(months: Long): String
    fun timeYearsAgo(years: Long): String
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> {
    error("No AppStrings provided")
}
