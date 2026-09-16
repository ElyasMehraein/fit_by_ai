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
    val target: String? = null,
    val movementPattern: String? = null,
    val targetMuscle: String? = null,
    val day: Int? = 1
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

    suspend fun updateExerciseImage(exerciseId: String, title: String, imageUrl: String) {
        val tasks = dao.getTasks()
        val updatedTasks = tasks.map { task ->
            val matchesId = exerciseId.isNotBlank() && task.exerciseId.equals(exerciseId, ignoreCase = true)
            val matchesTitle = title.isNotBlank() && task.title.equals(title, ignoreCase = true)
            if (matchesId || matchesTitle) {
                task.copy(images = listOf(imageUrl))
            } else {
                task
            }
        }
        dao.insertTasks(updatedTasks)
    }

    suspend fun deleteTask(taskId: String) {
        dao.deleteTask(taskId)
    }

    suspend fun generateAiPrompt(
        weight: String,
        waist: String,
        sleep: String,
        energy: Int,
        rpe: Int,
        pain: String,
        feedback: String,
        muscleSoreness: String = "نرمال",
        jointPain: String = "بدون درد مفصلی",
        language: com.fitbyai.app.i18n.AppLanguage = com.fitbyai.app.i18n.AppLanguage.DEFAULT
    ): String {
        val profile = dao.getUserProfile()
        val historyList = dao.getHistory()
        val weekNumber = historyList.size + 1
        val currentTasks = dao.getTasks()
        val daysCount = profile?.daysPerWeek?.filter { it.isDigit() }?.toIntOrNull()?.coerceAtLeast(1) ?: 4

        if (language != com.fitbyai.app.i18n.AppLanguage.PERSIAN) {
            // English / International AI Prompt
            val currentWeekWorkoutText = if (currentTasks.isNotEmpty()) {
                val grouped = currentTasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
                val doneCount = currentTasks.count { it.completed }
                val totalCount = currentTasks.size
                val rate = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100).toInt() else 0

                val lines = grouped.map { (_, tasks) ->
                    val first = tasks.first()
                    val completedInGroup = tasks.count { it.completed }
                    val muscleStr = if (first.targetMuscle.isNotBlank()) " | Muscle: ${first.targetMuscle}" else ""
                    val dayStr = " [Day ${first.day}]"
                    "  • ${first.title}$dayStr: ${tasks.size} sets (Target: ${first.targetPerSet.ifEmpty { "Execute" }}$muscleStr) | Status: $completedInGroup of ${tasks.size} sets completed"
                }.joinToString("\n")

                """
                Recent Workout Program & Performance:
                - Total sets completed: $doneCount of $totalCount sets ($rate%)
                - Exercises and sets trained:
                $lines
                """.trimIndent()
            } else {
                "Recent Workout Program: No workouts in queue yet (or this is your first training week)."
            }

            val pastWeeksHistoryText = if (historyList.isNotEmpty()) {
                historyList.joinToString("\n\n") { h ->
                    val exText = if (h.exerciseSummary.isNotBlank()) "\n  • Summary: ${h.exerciseSummary}" else ""
                    val soreText = if (h.muscleSoreness.isNotBlank()) " | Soreness: ${h.muscleSoreness}" else ""
                    val jointText = if (h.jointPain.isNotBlank()) " | Joint pain: ${h.jointPain}" else ""
                    """
                    - Week ${h.week} (Date: ${h.date}):
                      • Weight: ${h.weight} kg | Waist: ${h.waist} cm
                      • Recovery: Sleep: ${h.sleep} | Energy: ${h.energy}/10 | RPE: ${h.rpe}/10 | Pain: ${h.pain}$soreText$jointText
                      • Sets completed: ${h.completedSets} of ${h.totalSets} sets (${h.completionRate}%)
                      • Feedback: ${h.feedback.ifEmpty { "None" }}$exText
                    """.trimIndent()
                }
            } else {
                "No previous weeks recorded yet (first week)."
            }

            val jointPainNotice = if (jointPain.isNotBlank() && jointPain != "بدون درد مفصلی" && jointPain != "No Joint Pain") {
                "⚠️ Biomechanical Warning: The user reported tendon/joint pain in: \"$jointPain\". Replace heavy axial/joint stress movements with safer biomechanical alternatives."
            } else {
                "Joints Status: No acute joint or tendon pain reported."
            }

            return """
You are an expert PhD in Kinesiology, Exercise Science, and Strength & Conditioning.
I have completed my previous workout week. Here are my latest physical measurements, recovery metrics, and feedback:

- Current Working Week: $weekNumber
- New Weight: $weight kg
- New Waist: $waist cm
- Average Sleep: $sleep
- Energy Level (1-10): $energy
- Workout Difficulty / RPE (1-10): $rpe
- Muscle Soreness (DOMS): ${muscleSoreness.ifEmpty { "Normal" }}
- Joint/Tendon Pain: ${jointPain.ifEmpty { "None" }}
- User Feedback: ${feedback.ifEmpty { "None" }}

$jointPainNotice

========================================
$currentWeekWorkoutText
========================================

My Initial & Current Profile:
- Height: ${profile?.height ?: "-"} cm | Age: ${profile?.age ?: "-"} | Gender: ${profile?.gender ?: "-"}
- Base Weight: ${profile?.baseWeight ?: "-"} kg | Base Waist: ${profile?.baseWaist ?: "-"} cm | Target Weight: ${profile?.targetWeight?.ifEmpty { "-" } ?: "-"} kg
- Primary Goal: ${profile?.goal ?: "-"}
- Experience: ${profile?.experience ?: "-"} years
- Workout Days per Week: $daysCount days
- Session Duration: ${profile?.sessionDuration ?: "-"} minutes
- Daily Activity Level: ${profile?.activityLevel?.ifEmpty { "-" } ?: "-"}
- Available Equipment: ${profile?.equipment ?: "-"}
- Injuries & Limitations: ${profile?.limitations ?: "-"}
- Health Conditions: ${profile?.healthConditions?.ifEmpty { "None" } ?: "None"}

========================================
Performance History:
$pastWeeksHistoryText
========================================

Scientific Guidelines for Designing the New Program:
1. Ask me to send an updated physique photo with good lighting and no filters.
2. Compare visual physique changes, waist, scale weight, sleep, and fatigue markers according to Progressive Overload.
3. If high fatigue or joint soreness is reported, program a Deload week.
4. **Reps in Reserve (RIR)**: In `targetPerSet`, always specify the target repetitions and appropriate RIR (e.g. "10-12 reps (RIR 2)").
5. **Target Muscle (`targetMuscle`)**: Explicitly designate the primary target muscle (e.g., Chest, Back, Legs, Shoulders, Arms, Abs).
6. **Scientific Volume (MEV to MAV)**: Scale weekly volume based on recovery, experience, and lifestyle.
7. **Workout Day Allocation (`day`)**: The user has specified **$daysCount workout days per week**. You MUST organize the exercises into these specific $daysCount workout days (e.g. Day 1, Day 2, up to Day $daysCount). In each exercise object, you MUST provide the `"day"` integer property (from 1 to $daysCount) designating which workout day the exercise belongs to. The `weeklySets` key represents the number of sets for that exercise on that day.
8. Exercise ID (`id`) must be a clean snake_case English name (e.g., bench_press, barbell_squat, dumbbell_row, lat_pulldown, plank).
9. Output the final workout program ONLY as a valid JSON object matching the exact structure below (no markdown wrapping or extra text):

{
  "exercises": [
    {
      "id": "bench_press",
      "title": "Barbell Bench Press",
      "day": 1,
      "description": "Control the eccentric phase and touch mid-chest with elbows at 45-75 degrees",
      "weeklySets": 4,
      "targetPerSet": "10-12 reps (RIR 2)",
      "targetMuscle": "Chest"
    },
    {
      "id": "incline_dumbbell_press",
      "title": "Incline Dumbbell Press",
      "day": 1,
      "description": "Target upper clavicular pec fibers with 30 degree bench angle",
      "weeklySets": 3,
      "targetPerSet": "10-12 reps (RIR 2)",
      "targetMuscle": "Chest"
    },
    {
      "id": "barbell_squat",
      "title": "Barbell Back Squat",
      "day": 2,
      "description": "Full depth squat maintaining neutral spine and thoracic extension",
      "weeklySets": 4,
      "targetPerSet": "8-10 reps (RIR 2)",
      "targetMuscle": "Legs"
    }
  ]
}
""".trimIndent()
        }

        // Persian AI Prompt
        val currentWeekWorkoutText = if (currentTasks.isNotEmpty()) {
            val grouped = currentTasks.groupBy { if (it.exerciseId.isNotBlank()) it.exerciseId else it.title }
            val doneCount = currentTasks.count { it.completed }
            val totalCount = currentTasks.size
            val rate = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100).toInt() else 0

            val lines = grouped.map { (_, tasks) ->
                val first = tasks.first()
                val completedInGroup = tasks.count { it.completed }
                val muscleStr = if (first.targetMuscle.isNotBlank()) " | عضله: ${first.targetMuscle}" else ""
                val dayStr = " [روز ${first.day}]"
                "  • ${first.title}$dayStr: ${tasks.size} ست (هدف هر ست: ${first.targetPerSet.ifEmpty { "اجرا" }}$muscleStr) | وضعیت: $completedInGroup از ${tasks.size} ست انجام شد"
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

        val pastWeeksHistoryText = if (historyList.isNotEmpty()) {
            historyList.joinToString("\n\n") { h ->
                val exText = if (h.exerciseSummary.isNotBlank()) "\n  • حرکات تمرین‌شده: ${h.exerciseSummary}" else ""
                val soreText = if (h.muscleSoreness.isNotBlank()) " | کوفتگی عضلانی: ${h.muscleSoreness}" else ""
                val jointText = if (h.jointPain.isNotBlank()) " | درد مفصلی: ${h.jointPain}" else ""
                """
                - هفته ${h.week} (تاریخ: ${h.date}):
                  • وزن: ${h.weight} کیلوگرم | دور شکم: ${h.waist} سانتی‌متر
                  • ریکاوری: خواب: ${h.sleep} | انرژی: ${h.energy}/10 | سختی RPE: ${h.rpe}/10 | وضعیت درد: ${h.pain}$soreText$jointText
                  • ست‌های تکمیل‌شده: ${h.completedSets} از ${h.totalSets} ست (${h.completionRate}%)
                  • بازخورد کاربر: ${h.feedback.ifEmpty { "ثبت نشده" }}$exText
                """.trimIndent()
            }
        } else {
            "هنوز سابقه هفته‌های قبلی ثبت نشده است (این اولین هفته تمرینی است)."
        }

        val jointPainNotice = if (jointPain.isNotBlank() && jointPain != "بدون درد مفصلی") {
            "⚠️ هشدار ارزیابی بیومکانیک: کاربر درد در مفاصل/تاندون‌های «$jointPain» را گزارش داده است. حتماً حرکات پرفشار روی این مفصل را با گزینه‌های ایمن‌تر جایگزین کن."
        } else {
            "وضعیت مفاصل: هیچ درد مفصلی/تاندونی حادی گزارش نشده است."
        }

        return """
تو یک متخصص بدنسازی، بیومکانیک و علوم ورزشی (PhD in Kinesiology & Exercise Science) هستی.
من یک هفته برنامه تمرینی قبلی را کامل کرده‌ام. اطلاعات سنجش بدنی، ریکاوری، وضعیت مفاصل و بازخورد هفته اخیر من:

- هفته کاری فعلی: $weekNumber
- وزن جدید: $weight کیلوگرم
- دور شکم جدید: $waist سانتی‌متر
- میانگین خواب شبانه‌روز: $sleep
- سطح انرژی بدنی (1 تا 10): $energy
- میزان سختی تمرین / RPE (1 تا 10): $rpe
- کوفتگی عضلانی طبیعی (DOMS): ${muscleSoreness.ifEmpty { "نرمال" }}
- وضعیت دردهای مفصلی/تاندونی: ${jointPain.ifEmpty { "بدون درد مفصلی" }}
- بازخورد و حس کار با برنامه قبلی: ${feedback.ifEmpty { "ثبت نشده" }}

$jointPainNotice

========================================
$currentWeekWorkoutText
========================================

شناسنامه اولیه و کامل من:
- قد: ${profile?.height ?: "-"} cm | سن: ${profile?.age ?: "-"} | جنسیت: ${profile?.gender ?: "-"}
- وزن پایه: ${profile?.baseWeight ?: "-"} kg | دور شکم پایه: ${profile?.baseWaist ?: "-"} cm | وزن هدف: ${profile?.targetWeight?.ifEmpty { "-" } ?: "-"} kg
- هدف اصلی: ${profile?.goal ?: "-"}
- سابقه تمرینی: ${profile?.experience?.let { if (it.isNotBlank() && it.all { c -> c.isDigit() }) "$it سال" else it } ?: "-"}
- روزهای تمرین در هفته: ${daysCount} روز در هفته
- زمان در دسترس هر جلسه: ${profile?.sessionDuration?.let { if (it.isNotBlank() && it.all { c -> c.isDigit() }) "$it دقیقه" else it } ?: "-"}
- سطح فعالیت روزمره: ${profile?.activityLevel?.ifEmpty { "-" } ?: "-"}
- تجهیزات در دسترس: ${profile?.equipment ?: "-"}
- آسیب‌های قبلی و محدودیت‌ها: ${profile?.limitations ?: "-"}
- بیماری خاص یا داروهای مصرفی: ${profile?.healthConditions?.ifEmpty { "ندارد" } ?: "ندارد"}

========================================
تاریخچه پیشرفت و عملکرد کامل هفته‌های گذشته:
$pastWeeksHistoryText
========================================

دستورالعمل‌های علمی جهت طراحی برنامه جدید:
۱. در ابتدا از من بخواه یک عکس جدید از هیکلم با لباس مناسب، نور خوب و بدون فیلتر برات بفرستم.
۲. پس از دریافت عکس، روند تغییرات ظاهری، وزن، دور شکم، خواب، انرژی و بازخوردهای من را دقیقاً مقایسه کن.
۳. بر اساس اصل اضافه بار تدریجی (Progressive Overload)، شدت (RPE)، تعداد ست‌ها یا حجم تمرین را تنظیم کرده یا در صورت خستگی شدید/درد مفصلی هفته دِلود (Deload) تجویز کن.
۴. **تکرار در ذخیره (RIR)**: در کلید targetPerSet علاوه بر تعداد تکرار، حتماً مقدار RIR مناسب (مثلاً RIR 1 تا 3) را ذکر کن (مثلاً: «۱۰ الی ۱۲ تکرار (RIR 2)»).
۵. **عضله هدف (targetMuscle)** را برای هر حرکت به صورت شفاف تعیین کن (از مقادیر استاندارد مانند: سینه، پشت، پا، شانه، بازو، شکم).
۶. **محاسبه هوشمند و علمی حجم ست‌های هفتگی (Flexible Volume Calculation - MEV to MAV)**: حجم ست‌های هفتگی نباید صلب باشد؛ بلکه بر اساس سطح آمادگی کاربر، مقدار تحرک روزمره، سابقه ورزشی و سطح ریکاوری تعیین شود.
۷. **تفکیک حرکات بر اساس روزهای تمرینی (کلید day)**: کاربر در اطلاعات خود اعلام کرده است که **$daysCount روز در هفته** تمرین می‌کند. بنابراین حرکات برنامه تمرینی جدید را دقیقاً بین این $daysCount روز تفکیک و توزیع کن (مثلاً روز اول: سینه و پشت‌بازو، روز دوم: پشت و جلوبازو، روز سوم: پا و شکم و...). برای هر حرکت در آبجکت JSON، حتماً کلید `"day"` را با عدد صحیح روز تمرینی (از ۱ تا $daysCount) ثبت کن. کلید `weeklySets` نشان‌دهنده تعداد ست‌های آن حرکت در همان روز است.
۸. شناسه هر حرکت (id) باید اسم دقیق انگلیسی مانند bench_press، barbell_squat، push_up، dumbbell_bicep_curl، lat_pulldown، plank، deadlift و... باشد.
۹. خروجی نهایی برنامه تمرینی هفته جدید را فقط و فقط در قالب یک آبجکت معتبر JSON مطابق ساختار زیر ارسال کن (بدون هیچ متن اضافی قبل یا بعد از کد):

{
  "exercises": [
    {
      "id": "bench_press",
      "title": "پرس سینه با هالتر",
      "day": 1,
      "description": "توضیحات کامل تکنیک اجرای صحیح و کنترل فاز منفی",
      "weeklySets": 4,
      "targetPerSet": "۱۰ الی ۱۲ تکرار (RIR 2)",
      "targetMuscle": "سینه"
    },
    {
      "id": "incline_dumbbell_press",
      "title": "پرس سینه بالاسینه با دمبل",
      "day": 1,
      "description": "تمرکز روی بخش بالایی سینه و کنترل دامنه حرکت",
      "weeklySets": 4,
      "targetPerSet": "۱۰ الی ۱۲ تکرار (RIR 2)",
      "targetMuscle": "سینه"
    },
    {
      "id": "barbell_squat",
      "title": "اسکوات با هالتر",
      "day": 2,
      "description": "اجرای کامل با حفظ قوس طبیعی کمر",
      "weeklySets": 4,
      "targetPerSet": "۸ الی ۱۰ تکرار (RIR 2)",
      "targetMuscle": "پا"
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
        feedback: String,
        muscleSoreness: String = "نرمال",
        jointPain: String = "بدون درد مفصلی"
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
                    exerciseSummary = exerciseSummaryStr,
                    muscleSoreness = muscleSoreness,
                    jointPain = jointPain
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
                            completed = false,
                            movementPattern = ex.movementPattern ?: "",
                            targetMuscle = ex.targetMuscle ?: "",
                            day = ex.day ?: 1
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
