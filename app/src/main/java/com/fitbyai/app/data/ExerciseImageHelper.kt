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

    fun getExerciseImages(exerciseId: String, title: String = "", images: List<String> = emptyList()): List<String> {
        val validProvided = images.filter {
            it.isNotBlank() && it.startsWith("http") &&
                    !it.contains("example.com") &&
                    !it.contains("photo-example")
        }

        if (validProvided.size >= 5) {
            return validProvided.take(5)
        }

        val result = mutableListOf<String>()
        result.addAll(validProvided)

        val cleanedId = exerciseId.lowercase(Locale.ROOT)
            .trim()
            .replace("-", "_")
            .replace(" ", "_")

        val pathName = EXERCISE_MAP[cleanedId] ?: run {
            val searchKey = "$cleanedId ${title.lowercase(Locale.ROOT)}"
            when {
                searchKey.contains("squat") -> "Barbell_Squat"
                searchKey.contains("press") && searchKey.contains("bench") -> "Barbell_Bench_Press"
                searchKey.contains("press") && searchKey.contains("shoulder") -> "Dumbbell_Shoulder_Press"
                searchKey.contains("deadlift") -> "Barbell_Deadlift"
                searchKey.contains("curl") -> "Dumbbell_Bicep_Curl"
                searchKey.contains("row") -> "Barbell_Bent_Over_Row"
                searchKey.contains("pulldown") -> "Lat_Pulldown"
                searchKey.contains("pull") -> "Pull-up"
                searchKey.contains("pushup") || searchKey.contains("push_up") -> "Push-up"
                searchKey.contains("plank") -> "Plank"
                searchKey.contains("lunge") -> "Dumbbell_Lunge"
                searchKey.contains("raise") -> "Dumbbell_Lateral_Raise"
                else -> cleanedId.split("_").joinToString("_") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                }
            }
        }

        // Add FreeExerciseDB step 0 and step 1
        val dbUrl0 = "$BASE_URL/$pathName/0.jpg"
        val dbUrl1 = "$BASE_URL/$pathName/1.jpg"

        if (!result.contains(dbUrl0)) result.add(dbUrl0)
        if (!result.contains(dbUrl1)) result.add(dbUrl1)

        // Fill remaining slots up to 5 with high quality exercise variations
        val cleanTag = pathName.replace("_", "").replace("-", "").lowercase()
        var variationIndex = 1
        while (result.size < 5) {
            val altUrl = when (result.size % 3) {
                0 -> "https://loremflickr.com/600/600/$cleanTag,fitness/$variationIndex"
                1 -> "https://loremflickr.com/600/600/$cleanTag,gym/$variationIndex"
                else -> "https://loremflickr.com/600/600/$cleanTag,workout/$variationIndex"
            }
            if (!result.contains(altUrl)) {
                result.add(altUrl)
            }
            variationIndex++
        }

        return result.take(5)
    }

    fun getExerciseImageUrl(exerciseId: String, title: String = "", images: List<String> = emptyList()): String {
        return getExerciseImages(exerciseId, title, images).first()
    }
}
