package tekin.luetfi.resume.util


import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tekin.luetfi.resume.R
import tekin.luetfi.resume.domain.model.Cv
import tekin.luetfi.resume.domain.model.PeriodInfo
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor


/**
 * Constants
 */
const val CV_BASE_URL = "https://lutfitek.in/"
const val OPEN_ROUTER_AI_BASE_URL = "https://openrouter.ai/api/v1/"

const val REPORTS_DATABASE = "rdb"
const val REPORTS_TABLE = "rt"

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
        else -> "Loading Content"
    }
}


fun ClipboardManager.getTextOrNull(context: Context): String? {
    val data = primaryClip ?: return null
    if (data.itemCount == 0) return null
    return data.getItemAt(0).coerceToText(context).toString()
}

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
}