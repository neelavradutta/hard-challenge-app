package com.rain.dailygrind.data

data class ChecklistItem(
    val id: Int,
    val label: String,
    val duration: String?,
    val isChecked: Boolean = false,
    val note: String = "",
    val subChecked: List<String> = emptyList()
)

data class DailyLog(
    val date: String,
    val items: List<ChecklistItem>,
    val whatIDid: List<String> = listOf("", ""),
    // true = user picked date manually; skip auto-reset to today
    val dateManual: Boolean = false
)

object Defaults {
    const val SUBJECTS_ID = 5
    const val JOBS_ID = 6
    const val SLEEP_ID = 8
    const val WAKE_ID = 9
    const val SCREEN_ID = 10
    val subjects = listOf("CN", "OS", "OOPS", "System Design")
    val checklist = listOf(
        ChecklistItem(1, "DSA", "2 hrs"),
        ChecklistItem(2, "Development", "1.5 hrs"),
        ChecklistItem(3, "Database", "1 hr"),
        ChecklistItem(4, "Aptitude", "1 hr"),
        ChecklistItem(5, "Subjects (any 2)", "2 hrs"),
        ChecklistItem(6, "Job applied", null),
        ChecklistItem(7, "Walk", "1 hr"),
        ChecklistItem(8, "Sleep before 2 AM", null),
        ChecklistItem(9, "Wake up before 8:30 AM", null),
        ChecklistItem(10, "Screen time < 3 hrs", null)
    )
}
