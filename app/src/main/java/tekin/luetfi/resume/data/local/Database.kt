package tekin.luetfi.resume.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [JobReportEntity::class], version = 1, exportSchema = false)
@TypeConverters(JobReportTypeConverter::class)
abstract class ReportsDatabase: RoomDatabase(){
    abstract val dao: JobReportDao
}