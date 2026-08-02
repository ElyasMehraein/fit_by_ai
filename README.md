# Fit by AI | سورس‌کد ۱۰۰٪ نیتیو کاتلین اندروید (Pure Kotlin & Jetpack Compose)

این پروژه اکنون **به طور کامل و ۱۰۰٪ با زبان کاتلین (Kotlin)** و جدیدترین معماری نیتیو گوگل اندروید پیاده‌سازی شده است.

---

## 🏗️ معماری و فناوری‌های نیتیو کاتلین (Kotlin Native Stack)

- **زبان برنامه نویسی**: Kotlin 1.9+
- **رابط کاربری نیتیو (UI Framework)**: Jetpack Compose (Material 3)
- **پایگاه داده نیتیو**: Room Database
- **معماری نرم‌افزار**: MVVM (ViewModel + StateFlow + Repository Pattern)
- **برنامه‌نویسی ناهمگام**: Kotlin Coroutines & Flow
- **لود تصویر ناهمگام**: Coil Compose Library

---

## 📁 ساختار سورس‌کد کاتلین (`android/app/src/main/java/com/fitbyai/app/`)

- 📂 **`data/`**:
  - `Entities.kt`: انتیتی‌های دیتابیس Room برای پروفایل کاربر، ست‌های تمرینی، تاریخچه پیشرفت و زمان‌بندی هفته‌ها.
  - `WorkoutDao.kt`: متدهای دسترسی ناهمگام به دیتابیس Room با Kotlin Coroutines & Flow.
  - `AppDatabase.kt`: پیکربندی پایگاه داده محلی نیتیو Room.
  - `WorkoutRepository.kt`: لایه ریپازیتوری برای پردازش داده‌ها، ساخت پرامپت هوشمند و تحلیل JSON.

- 📂 **`ui/`**:
  - `WorkoutViewModel.kt`: مدیریت وضعیت‌های برنامه با `StateFlow<WorkoutUiState>`.
  - 📂 **`theme/`**: پلت رنگی تاریک و مدرن (Slate/Emerald) در Material 3.
  - 📂 **`screens/`**:
    - `MainWorkoutScreen.kt`: صفحه اصلی شامل هدر، تایمر معکوس، نوار پیشرفت، تب‌های صف تمرین/انجام‌شده و لیست کارت‌های تمرینی در Jetpack Compose.
  - 📂 **`dialogs/`**:
    - `ProfileDialog.kt`: دیالوگ ثبت و ویرایش پروفایل اولیه کاربر.
    - `WeeklyReviewDialog.kt`: دیالوگ ارزیابی ۷ روزه، **تولید پرامپت هوش مصنوعی (درخواست ۳ عکس + تاریخچه کامل)**، کپی به Clipboard و وارد کردن JSON.
    - `HistoryDialog.kt`: دیالوگ مشاهده پیشرفت و تاریخچه هفته‌ها.

- 📄 **`MainActivity.kt`**: نقطه ورود نیتیو برنامه با `ComponentActivity` و `setContent { FitByAiTheme { MainWorkoutScreen() } }`.

---

## 🛠️ ساخت فایل APK در Android Studio

1. پوشه‌ی **`android`** را در **Android Studio** باز کنید (`File -> Open -> select android directory`).
2. اجازه دهید Gradle فرآیند Sync را انجام دهد.
3. از منوی بالا مسیر زیر را طی کنید:
   `Build -> Build Bundle(s) / APK(s) -> Build APK(s)`
4. فایل `.apk` نیتیو کاتلین شما آماده نصب روی دستگاه‌های اندرویدی است!
