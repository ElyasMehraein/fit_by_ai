package com.fitbyai.app.i18n

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean,
    val flag: String
) {
    ENGLISH("en", "English", "English", false, "🇺🇸"),
    PERSIAN("fa", "Persian", "فارسی", true, "🇮🇷"),
    JAPANESE("ja", "Japanese", "日本語", false, "🇯🇵"),
    KOREAN("ko", "Korean", "한국어", false, "🇰🇷"),
    GERMAN("de", "German", "Deutsch", false, "🇩🇪"),
    SPANISH("es", "Spanish", "Español", false, "🇪🇸");

    companion object {
        val DEFAULT = ENGLISH

        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
        }
    }
}
