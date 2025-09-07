package tekin.luetfi.resume


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import tekin.luetfi.resume.domain.model.Cv
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor


/**
 * Constants
 */
const val BASE_URL = "https://lutfitek.in/"


data class PeriodInfo(
    val startLabel: String,         // "MAY 2025"
    val endLabel: String,           // "PRESENT" or "AUG 2024"
    val totalMonths: Int,           // inclusive
    val durationText: String        // "2 years 3 months"
)

private val ymFmtIn = DateTimeFormatter.ofPattern("yyyy-MM")
private val ymOut = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)

fun computePeriod(period: String, now: LocalDate = LocalDate.now()): PeriodInfo {
    val parts = period.split(Regex("\\s*(?:to|–)\\s*"), limit = 2)
    val startYm = YearMonth.parse(parts[0], ymFmtIn)

    val (endYear, endMonth, endLabel) = if (parts.getOrNull(1)
            ?.equals("now", ignoreCase = true) == true
    ) {
        Triple(now.year, now.monthValue, "PRESENT")
    } else {
        val endYm = YearMonth.parse(parts[1], ymFmtIn)
        Triple(endYm.year, endYm.monthValue, endYm.format(ymOut).uppercase(Locale.ENGLISH))
    }

    val totalMonths = (endYear - startYm.year) * 12 + (endMonth - startYm.monthValue) + 1
    val years = floor(totalMonths / 12.0).toInt()
    val months = totalMonths % 12
    val duration = buildString {
        if (years > 0) append("$years year${if (years > 1) "s" else ""}")
        if (months > 0) {
            if (isNotEmpty()) append(' ')
            append("$months month${if (months > 1) "s" else ""}")
        }
        if (isEmpty()) append("0 months")
    }

    return PeriodInfo(
        startLabel = startYm.format(ymOut).uppercase(Locale.ENGLISH),
        endLabel = endLabel,
        totalMonths = totalMonths,
        durationText = duration
    )
}

@Composable
fun Cv.openToOpportunities(): String {
    return when (openToOpportunities) {
        "actively_looking" -> stringResource(R.string.op_active)
        "passive" -> stringResource(R.string.op_passive)
        "not_interested" -> stringResource(R.string.op_not_interested)
        else -> ""
    }
}

sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
}