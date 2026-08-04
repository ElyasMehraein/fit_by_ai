package com.fitbyai.app.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ExerciseJson(
    val id: String,
    val title: String,
    val description: String?,
    val images: List<String>?,
    val weeklySets: Int,
    val targetPerSet: String? = null,
    val target: String? = null
)

data class ProgramJsonPayload(
    @SerializedName("exercises") val exercises: List<ExerciseJson>
)

class WorkoutRepository(private val dao: WorkoutDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.getUserProfileFlow()
    val profileHistoryFlow: Flow<List<ProfileHistoryEntity>> = dao.getProfileHistoryFlow()
    val tasksFlow: Flow<List<WorkoutTaskEntity>> = dao.getTasksFlow()
    val historyFlow: Flow<List<WeeklyHistoryEntity>> = dao.getHistoryFlow()
    val metadataFlow: Flow<WeeklyMetadataEntity?> = dao.getMetadataFlow()

    suspend fun saveUserProfile(newProfile: UserProfileEntity) {
        val oldProfile = dao.getUserProfile()
        if (oldProfile != null) {
            val historyItem = ProfileHistoryEntity(
                timestamp = System.currentTimeMillis(),
                height = oldProfile.height,
                age = oldProfile.age,
                gender = oldProfile.gender,
                goal = oldProfile.goal,
                baseWeight = oldProfile.baseWeight,
                baseWaist = oldProfile.baseWaist,
                experience = oldProfile.experience,
                daysPerWeek = oldProfile.daysPerWeek,
                equipment = oldProfile.equipment,
                limitations = oldProfile.limitations,
                targetWeight = oldProfile.targetWeight,
                sessionDuration = oldProfile.sessionDuration,
                activityLevel = oldProfile.activityLevel,
                healthConditions = oldProfile.healthConditions
            )
            dao.insertProfileHistory(historyItem)
        }
        dao.saveUserProfile(newProfile)
    }

    suspend fun updateTaskStatus(taskId: String, completed: Boolean) {
        dao.updateTaskStatus(taskId, completed)
    }

    suspend fun generateAiPrompt(
        weight: String,
        waist: String,
        sleep: String,
        energy: Int,
        rpe: Int,
        pain: String,
        feedback: String
    ): String {
        val profile = dao.getUserProfile()
        val historyList = dao.getHistory()
        val weekNumber = historyList.size + 1
        val currentTasks = dao.getTasks()

        // 1. Details of the recent/current workout program and completed sets
        val currentWeekWorkoutText = if (currentTasks.isNotEmpty()) {
            val grouped = currentTasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
            val doneCount = currentTasks.count { it.completed }
            val totalCount = currentTasks.size
            val rate = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100).toInt() else 0

            val lines = grouped.map { (_, tasks) ->
                val first = tasks.first()
                val completedInGroup = tasks.count { it.completed }
                "  • ${first.title}: ${tasks.size} ست (هدف هر ست: ${first.targetPerSet.ifEmpty { "اجرا" }}) | وضعیت: $completedInGroup از ${tasks.size} ست انجام شد"
            }.joinToString("\n")

            """
            برنامه تمرینی هفته اخیر و میزان انجام حرکات:
            - میزان کل ست‌های انجام‌شده: $doneCount از $totalCount ست ($rate%)
            - لیست حرکات و تعداد ست‌های تمرین‌شده در این هفته:
            $lines
            """.trimIndent()
        } else {
            "برنامه تمرینی اخیر: هنوز حرکتی در صف ثبت نشده است (یا این اولین هفته تمرینی شماست)."
        }

        // 2. Details of past weeks history
        val pastWeeksHistoryText = if (historyList.isNotEmpty()) {
            historyList.joinToString("\n\n") { h ->
                val exText = if (h.exerciseSummary.isNotBlank()) "\n  • حرکات تمرین‌شده: ${h.exerciseSummary}" else ""
                """
                - هفته ${h.week} (تاریخ: ${h.date}):
                  • وزن: ${h.weight} کیلوگرم | دور کمر: ${h.waist} سانتی‌متر
                  • ریکاوری: خواب: ${h.sleep} | انرژی: ${h.energy}/10 | سختی RPE: ${h.rpe}/10 | وضعیت درد: ${h.pain}
                  • ست‌های تکمیل‌شده: ${h.completedSets} از ${h.totalSets} ست (${h.completionRate}%)
                  • بازخورد کاربر: ${h.feedback.ifEmpty { "ثبت نشده" }}$exText
                """.trimIndent()
            }
        } else {
            "هنوز سابقه هفته‌های قبلی ثبت نشده است (این اولین هفته تمرینی است)."
        }

        return """
تو یک مربی ارشد و حرفه‌ای بدنسازی و فیتنس هستی.
من یک هفته برنامه تمرینی قبلی را کامل کرده‌ام. اطلاعات سنجش بدنی، ریکاوری و بازخورد هفته اخیر من:

- هفته کاری فعلی: $weekNumber
- وزن جدید: $weight کیلوگرم
- دور کمر جدید: $waist سانتی‌متر
- میانگین خواب شبانه‌روز: $sleep
- سطح انرژی بدنی (1 تا 10): $energy
- میزان سختی تمرین / RPE (1 تا 10): $rpe
- وضع دردهای جدید یا عضلانی: ${pain.ifEmpty { "بدون درد" }}
- بازخورد و حس کار با برنامه قبلی: ${feedback.ifEmpty { "ثبت نشده" }}

========================================
$currentWeekWorkoutText
========================================

شناسنامه اولیه و کامل من:
- قد: ${profile?.height ?: "-"} cm | سن: ${profile?.age ?: "-"} | جنسیت: ${profile?.gender ?: "-"}
- وزن پایه: ${profile?.baseWeight ?: "-"} kg | دور کمر پایه: ${profile?.baseWaist ?: "-"} cm | وزن هدف: ${profile?.targetWeight?.ifEmpty { "-" } ?: "-"} kg
- هدف اصلی: ${profile?.goal ?: "-"}
- سابقه تمرینی: ${profile?.experience?.let { if (it.isNotBlank() && it.all { c -> c.isDigit() }) "$it ماه" else it } ?: "-"}
- روزهای تمرین در هفته: ${profile?.daysPerWeek?.let { if (it.isNotBlank() && it.all { c -> c.isDigit() }) "$it روز در هفته" else it } ?: "-"}
- زمان در دسترس هر جلسه: ${profile?.sessionDuration?.let { if (it.isNotBlank() && it.all { c -> c.isDigit() }) "$it دقیقه" else it } ?: "-"}
- سطح فعالیت روزمره: ${profile?.activityLevel?.ifEmpty { "-" } ?: "-"}
- تجهیزات در دسترس: ${profile?.equipment ?: "-"}
- آسیب‌های قبلی و محدودیت‌ها: ${profile?.limitations ?: "-"}
- بیماری خاص یا داروهای مصرفی: ${profile?.healthConditions?.ifEmpty { "ندارد" } ?: "ندارد"}

========================================
تاریخچه پیشرفت و عملکرد کامل هفته‌های گذشته:
$pastWeeksHistoryText
========================================

دستورالعمل مهم جهت طراحی برنامه جدید:
۱. لطفاً در ابتدا از من بخواه یک عکس جدید از هیکلم با لباس مناسب، نور خوب و بدون فیلتر برات بفرستم.
۲. پس از دریافت عکس، روند تغییرات ظاهری، وزن، دور کمر، خواب، انرژی و بازخوردهای من را دقیقاً مقایسه کن.
۳. بر اساس حرکات هفته قبل و میزان ست‌های انجام‌شده، اصل اضافه بار تدریجی (Progressive Overload) را رعایت کن. در صورت ضرورت، شدت (RPE)، تعداد ست‌ها یا حجم تمرین را تنظیم کرده یا در صورت نیاز هفته دِلود (Deload) تجویز کن.
۴. برنامه تمرینی کلاً به‌صورت یک بانک/حجم کلی ست‌های هفتگی (Weekly Sets) باشد و به روزهای خاص تقسیم نشود؛ به طوری که کاربر مختار باشد تمام ست‌ها را در ۱ روز بزند یا بین ۲ تا ۶ روز تقسیم کند و محدودیتی نداشته باشد.
۵. شناسه هر حرکت (id) باید اسم دقیق و استاندارد انگلیسی حرکت مانند bench_press، barbell_squat، push_up، dumbbell_bicep_curl، lat_pulldown، plank، deadlift و... باشد.
۶. برای هر حرکت حتماً مقدار/هدف دقیق هر ست (مثلاً «۱۰ الی ۱۲ تکرار»، «۴۵ ثانیه»، «۲۰ شنا»، «۱۵ دقیقه پیاده‌روی» یا «تا ناتوانی») را در کلید targetPerSet مشخص کن.
۷. خروجی نهایی برنامه تمرینی هفته جدید را فقط و فقط در قالب یک آبجکت معتبر JSON مطابق ساختار زیر ارسال کن (بدون هیچ متن اضافی قبل یا بعد از کد):

{
  "exercises": [
    {
      "id": "bench_press",
      "title": "پرس سینه با هالتر",
      "description": "توضیحات کامل تکنیک اجرای صحیح",
      "weeklySets": 4,
      "targetPerSet": "۱۰ الی ۱۲ تکرار"
    }
  ]
}
""".trimIndent()
    }

    suspend fun importJsonProgram(
        jsonRaw: String,
        newWeight: String,
        newWaist: String,
        sleep: String,
        energy: Int,
        rpe: Int,
        pain: String,
        feedback: String
    ): Result<Unit> {
        return try {
            val payload = Gson().fromJson(jsonRaw, ProgramJsonPayload::class.java)
                ?: return Result.failure(Exception("فرمت JSON نامعتبر است."))

            if (payload.exercises.isEmpty()) {
                return Result.failure(Exception("آرایه exercises نباید خالی باشد."))
            }

            // Archive existing week if tasks exist
            val currentTasks = dao.getTasks()
            if (currentTasks.isNotEmpty()) {
                val doneCount = currentTasks.count { it.completed }
                val totalCount = currentTasks.size
                val historyList = dao.getHistory()
                val profile = dao.getUserProfile()

                val exerciseSummaryStr = currentTasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
                    .map { (_, tasks) ->
                        val first = tasks.first()
                        val done = tasks.count { it.completed }
                        "${first.title} (${tasks.size} ست - $done انجام شد)"
                    }.joinToString("، ")

                val historyRecord = WeeklyHistoryEntity(
                    week = historyList.size + 1,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                    weight = newWeight.toDoubleOrNull() ?: profile?.baseWeight?.toDoubleOrNull() ?: 0.0,
                    waist = newWaist.toDoubleOrNull() ?: profile?.baseWaist?.toDoubleOrNull() ?: 0.0,
                    sleep = sleep,
                    energy = energy,
                    rpe = rpe,
                    pain = pain.ifEmpty { "بدون درد" },
                    completedSets = doneCount,
                    totalSets = totalCount,
                    completionRate = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100).toInt() else 100,
                    feedback = feedback.ifEmpty { "تمرینات هفته انجام شد" },
                    exerciseCount = payload.exercises.size,
                    exerciseSummary = exerciseSummaryStr
                )
                dao.insertHistory(historyRecord)
            }

            // Generate new tasks
            val newTasks = mutableListOf<WorkoutTaskEntity>()
            val now = System.currentTimeMillis()

            payload.exercises.forEach { ex ->
                if (ex.weeklySets <= 0 || ex.id.isBlank() || ex.title.isBlank()) {
                    return Result.failure(Exception("اطلاعات id، title و weeklySets معتبر نیستند."))
                }

                val targetText = ex.targetPerSet ?: ex.target ?: ""

                for (s in 1..ex.weeklySets) {
                    newTasks.add(
                        WorkoutTaskEntity(
                            taskId = "${ex.id}-set-$s-$now-${(1000..9999).random()}",
                            exerciseId = ex.id,
                            title = ex.title,
                            description = ex.description ?: "",
                            targetPerSet = targetText,
                            images = emptyList(),
                            setNumber = s,
                            totalSets = ex.weeklySets,
                            completed = false
                        )
                    )
                }
            }

            // Save to DB
            dao.clearTasks()
            dao.insertTasks(newTasks)

            val weekMs = 7 * 24 * 60 * 60 * 1000L
            dao.saveMetadata(
                WeeklyMetadataEntity(
                    uploadTimestamp = now,
                    deadlineTimestamp = now + weekMs
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetAllData() {
        dao.clearTasks()
        dao.clearHistory()
        dao.clearMetadata()
    }
}
