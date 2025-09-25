package tekin.luetfi.resume.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tekin.luetfi.resume.REPORTS_DATABASE
import tekin.luetfi.resume.data.local.JobReportTypeConverter
import tekin.luetfi.resume.data.local.ReportsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, moshi: Moshi): ReportsDatabase =
        Room.databaseBuilder(
            context = context,
            klass = ReportsDatabase::class.java,
            name = REPORTS_DATABASE
        ).addTypeConverter(JobReportTypeConverter(moshi)).build()


    @Provides
    @Singleton
    fun provideDao(db: ReportsDatabase) = db.dao

}