package tekin.luetfi.resume.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import tekin.luetfi.resume.REPORTS_TABLE
import tekin.luetfi.resume.domain.model.MatchResponse

@Entity(tableName = REPORTS_TABLE)
data class JobReportEntity(
    @PrimaryKey
    val id: String,
    val result: MatchResponse
)


fun MatchResponse.toEntity() = JobReportEntity(
    id = job.title + job.company,
    result = this
)

@ProvidedTypeConverter
class JobReportTypeConverter(private val moshi: Moshi) {
    private val adapter = moshi.adapter(MatchResponse::class.java)

    @TypeConverter
    fun fromMatchResponse(value: MatchResponse?): String? {
        return value?.let { adapter.toJson(it) }
    }

    @TypeConverter
    fun toMatchResponse(value: String?): MatchResponse? {
        return value?.let { adapter.fromJson(it) }
    }
}