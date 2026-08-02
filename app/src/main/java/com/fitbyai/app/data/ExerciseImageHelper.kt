package com.fitbyai.app.data

import java.util.Locale

object ExerciseImageHelper {

    private const val BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises"

    private val EXERCISE_MAP = mapOf(
        // Chest
        "bench_press" to "Barbell_Bench_Press",
        "barbell_bench_press" to "Barbell_Bench_Press",
        "incline_bench_press" to "Barbell_Incline_Bench_Press",
        "dumbbell_bench_press" to "Dumbbell_Bench_Press",
        "dumbbell_incline_press" to "Dumbbell_Incline_Bench_Press",
        "push_up" to "Push-up",
        "pushup" to "Push-up",
        "chest_fly" to "Cable_Chest_Fly",
        "cable_fly" to "Cable_Chest_Fly",
        "dips" to "Triceps_Dip",

        // Legs
        "squat" to "Barbell_Squat",
        "barbell_squat" to "Barbell_Squat",
        "goblet_squat" to "Goblet_Squat",
        "leg_press" to "Leg_Press",
        "lunge" to "Dumbbell_Lunge",
        "lunges" to "Dumbbell_Lunge",
        "leg_extension" to "Leg_Extensions",
        "leg_curl" to "Seated_Leg_Curl",
        "calf_raise" to "Standing_Calf_Raise",
        "romanian_deadlift" to "Barbell_Romanian_Deadlift",

        // Back
        "deadlift" to "Barbell_Deadlift",
        "barbell_deadlift" to "Barbell_Deadlift",
        "lat_pulldown" to "Lat_Pulldown",
        "pull_up" to "Pull-up",
        "pullup" to "Pull-up",
        "chin_up" to "Chin-Up",
        "bent_over_row" to "Barbell_Bent_Over_Row",
        "barbell_row" to "Barbell_Bent_Over_Row",
        "dumbbell_row" to "Dumbbell_Row",
        "seated_cable_row" to "Seated_Cable_Row",

        // Shoulders
        "shoulder_press" to "Dumbbell_Shoulder_Press",
        "overhead_press" to "Barbell_Shoulder_Press",
        "military_press" to "Barbell_Shoulder_Press",
        "lateral_raise" to "Dumbbell_Lateral_Raise",
        "front_raise" to "Dumbbell_Front_Raise",
        "face_pull" to "Cable_Face_Pull",

        // Arms
        "bicep_curl" to "Dumbbell_Bicep_Curl",
        "barbell_curl" to "Barbell_Curl",
        "hammer_curl" to "Dumbbell_Hammer_Curl",
        "triceps_pushdown" to "Cable_Triceps_Pushdown",
        "skullcrusher" to "Barbell_Lying_Triceps_Extension",
        "triceps_extension" to "Dumbbell_Triceps_Extension",

        // Core / Abs
        "plank" to "Plank",
        "crunch" to "Crunch",
        "leg_raise" to "Hanging_Leg_Raise",
        "russian_twist" to "Russian_Twist"
    )

    fun getExerciseImageUrl(exerciseId: String, title: String = "", images: List<String> = emptyList()): String {
        // 1. Check if AI already provided a valid direct URL to a real photo
        val providedUrl = images.firstOrNull {
            it.isNotBlank() && it.startsWith("http") &&
                    !it.contains("example.com") &&
                    !it.contains("photo-example") &&
                    !it.contains("loremflickr.com")
        }
        if (providedUrl != null) {
            return providedUrl
        }

        // 2. Normalize exercise ID
        val cleanedId = exerciseId.lowercase(Locale.ROOT)
            .trim()
            .replace("-", "_")
            .replace(" ", "_")

        // 3. Match from verified dictionary
        EXERCISE_MAP[cleanedId]?.let { path ->
            return "$BASE_URL/$path/0.jpg"
        }

        // 4. Fallback search by key terms in ID or Title
        val searchKey = "$cleanedId ${title.lowercase(Locale.ROOT)}"
        return when {
            searchKey.contains("squat") -> "$BASE_URL/Barbell_Squat/0.jpg"
            searchKey.contains("press") && searchKey.contains("bench") -> "$BASE_URL/Barbell_Bench_Press/0.jpg"
            searchKey.contains("press") && searchKey.contains("shoulder") -> "$BASE_URL/Dumbbell_Shoulder_Press/0.jpg"
            searchKey.contains("deadlift") -> "$BASE_URL/Barbell_Deadlift/0.jpg"
            searchKey.contains("curl") -> "$BASE_URL/Dumbbell_Bicep_Curl/0.jpg"
            searchKey.contains("row") -> "$BASE_URL/Barbell_Bent_Over_Row/0.jpg"
            searchKey.contains("pulldown") -> "$BASE_URL/Lat_Pulldown/0.jpg"
            searchKey.contains("pull") -> "$BASE_URL/Pull-up/0.jpg"
            searchKey.contains("pushup") || searchKey.contains("push_up") -> "$BASE_URL/Push-up/0.jpg"
            searchKey.contains("plank") -> "$BASE_URL/Plank/0.jpg"
            searchKey.contains("lunge") -> "$BASE_URL/Dumbbell_Lunge/0.jpg"
            searchKey.contains("raise") -> "$BASE_URL/Dumbbell_Lateral_Raise/0.jpg"
            else -> {
                // Auto Title Case conversion for unknown exercise IDs
                val formattedPath = cleanedId.split("_").joinToString("_") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                }
                "$BASE_URL/$formattedPath/0.jpg"
            }
        }
    }
}
