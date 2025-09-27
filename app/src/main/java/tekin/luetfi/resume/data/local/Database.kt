package tekin.luetfi.resume.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import tekin.luetfi.resume.util.REPORTS_TABLE

@Database(entities = [JobReportEntity::class], version = 2, exportSchema = false)
@TypeConverters(JobReportTypeConverter::class)
abstract class ReportsDatabase: RoomDatabase(){
    abstract val dao: JobReportDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1) Add the column as NOT NULL with a constant default
        db.execSQL("""
            ALTER TABLE $REPORTS_TABLE
            ADD COLUMN added INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        // 2) Backfill existing rows with "now" in millis
        db.execSQL("""
            UPDATE $REPORTS_TABLE
            SET added = CAST(strftime('%s','now') AS INTEGER) * 1000
            WHERE added = 0
        """.trimIndent())
    }
}