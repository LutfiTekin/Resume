package tekin.luetfi.resume.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.data.repository.DefaultCvRepository
import tekin.luetfi.resume.domain.repository.CvRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {


    @Provides
    @Singleton
    fun provideApi(@CvApi retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides @Singleton
    fun provideCvRepository(api: Api): CvRepository = DefaultCvRepository(api)


}