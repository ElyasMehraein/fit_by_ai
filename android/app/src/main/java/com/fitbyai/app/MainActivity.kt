package com.fitbyai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fitbyai.app.data.AppDatabase
import com.fitbyai.app.data.WorkoutRepository
import com.fitbyai.app.ui.WorkoutViewModel
import com.fitbyai.app.ui.WorkoutViewModelFactory
import com.fitbyai.app.ui.screens.MainWorkoutScreen
import com.fitbyai.app.ui.theme.FitByAiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WorkoutViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = WorkoutRepository(database.workoutDao())
        WorkoutViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitByAiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainWorkoutScreen(viewModel = viewModel)
                }
            }
        }
    }
}
