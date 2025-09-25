package tekin.luetfi.resume.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tekin.luetfi.resume.REPORTS_TABLE

@Dao
interface JobReportDao {

    @Query("SELECT * FROM $REPORTS_TABLE ORDER BY added DESC")
    suspend fun loadReports(): List<JobReportEntity>

    @Query("SELECT * FROM $REPORTS_TABLE WHERE id = :id")
    suspend fun loadReport(id: String): JobReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReport(report: JobReportEntity)

    @Delete
    suspend fun deleteReport(report: JobReportEntity)

}