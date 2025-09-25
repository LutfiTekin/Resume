package tekin.luetfi.resume.domain.model

data class PeriodInfo(
    val startLabel: String,// "MAY 2025"
    val endLabel: String,// "PRESENT" or "AUG 2024"
    val totalMonths: Int,// inclusive
    val durationText: String// "2 years 3 months"
)