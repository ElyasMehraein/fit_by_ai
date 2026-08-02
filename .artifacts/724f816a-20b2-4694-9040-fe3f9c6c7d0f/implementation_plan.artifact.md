# Extreme Minimalist UX for Weekly Progress

The goal is to strip away all "government form" elements, unnecessary text, and redundant titles. We will transform the Weekly Review into a clean, action-oriented overlay that reuses existing data and stays out of the user's way.

## User Review Required

> [!IMPORTANT]
> **Minimalist Actions**: We are removing the dialog title and all explanatory paragraphs. The interface will focus on two primary actions: **Generating the AI Prompt** and **Importing the Program**.

> [!TIP]
> **Data Reuse**: Metrics like weight, waist, energy, and RPE will be displayed as compact, editable elements with sensible defaults. You won't have to "fill a form" unless something changed.

> [!NOTE]
> **Profile Integration**: A small "Edit Profile" shortcut will be added. Clicking it will take you directly to your main profile settings, keeping the weekly flow uncluttered.

## Proposed Changes

### UX Simplification
#### [MODIFY] [WeeklyReviewDialog.kt](file:///C:/Users/elyas/Desktop/fit_by_ai/app/src/main/java/com/fitbyai/app/ui/dialogs/WeeklyReviewDialog.kt)
- **Remove**: Title ("بروزرسانی هفتگی"), Description, and all section labels.
- **Compact Metrics**: Display Weight, Waist, Energy, and RPE in a single, high-density row or grid.
- **Action Buttons**:
  - One prominent button for "Generate AI Prompt".
  - A subtle text field for JSON input with a "Start Week" button.
- **Navigation**: Add a "Edit Profile" link/icon that triggers a callback to switch to the Profile Dialog.

#### [MODIFY] [MainWorkoutScreen.kt](file:///C:/Users/elyas/Desktop/fit_by_ai/app/src/main/java/com/fitbyai/app/ui/screens/MainWorkoutScreen.kt)
- Handle the `onEditProfile` callback from `WeeklyReviewDialog` to close the review and open the profile.

### Styling
- Adhere to the **Calm White** minimalist theme.
- Use iconography instead of long labels where intuitive.

## Verification Plan

### Manual Verification
- Deploy to an emulator/device.
- Verify the Weekly Review dialog is now significantly smaller and "quieter".
- Confirm that Generate Prompt uses the latest data without forced re-entry.
- Test the "Edit Profile" transition.
- Ensure the minimalist look matches the overall "FitByAI" aesthetic.
