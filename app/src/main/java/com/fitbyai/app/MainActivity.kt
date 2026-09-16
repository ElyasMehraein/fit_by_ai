package com.fitbyai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.fitbyai.app.data.AppDatabase
import com.fitbyai.app.data.WorkoutRepository
import com.fitbyai.app.i18n.LanguagePreferenceManager
import com.fitbyai.app.i18n.LocalAppStrings
import com.fitbyai.app.i18n.appStringsFor
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
        val languageManager = LanguagePreferenceManager.getInstance(applicationContext)

        setContent {
            val currentLanguage by languageManager.currentLanguage.collectAsState()
            val strings = remember(currentLanguage) { appStringsFor(currentLanguage) }
            val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalAppStrings provides strings,
                LocalLayoutDirection provides layoutDirection
            ) {
                FitByAiTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainWorkoutScreen(
                            viewModel = viewModel,
                            currentLanguage = currentLanguage,
                            onLanguageChanged = { languageManager.setLanguage(it) }
                        )
                    }
                }
            }
        }
    }
}
