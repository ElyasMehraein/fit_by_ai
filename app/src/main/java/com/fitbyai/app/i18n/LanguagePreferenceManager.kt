package com.fitbyai.app.i18n

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguagePreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fit_by_ai_i18n_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(loadInitialLanguage())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private fun loadInitialLanguage(): AppLanguage {
        val savedCode = prefs.getString(KEY_LANGUAGE, null)
        return if (savedCode != null) {
            AppLanguage.fromCode(savedCode)
        } else {
            // Default language is English as requested
            AppLanguage.ENGLISH
        }
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        _currentLanguage.value = language
    }

    companion object {
        private const val KEY_LANGUAGE = "selected_app_language"

        @Volatile
        private var INSTANCE: LanguagePreferenceManager? = null

        fun getInstance(context: Context): LanguagePreferenceManager {
            return INSTANCE ?: synchronized(this) {
                val instance = LanguagePreferenceManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
