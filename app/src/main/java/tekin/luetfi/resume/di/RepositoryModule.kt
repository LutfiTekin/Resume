package tekin.luetfi.resume.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import tekin.luetfi.resume.data.local.JobReportDao
import tekin.luetfi.resume.data.remote.Api
import tekin.luetfi.resume.data.remote.OpenRouterAiApi
import tekin.luetfi.resume.data.repository.DefaultCvRepository
import tekin.luetfi.resume.data.repository.DefaultJobAnalyzerRepository
import tekin.luetfi.resume.data.repository.DefaultModelsRepository
import tekin.luetfi.resume.data.repository.DefaultTechStackInfoRepository
import tekin.luetfi.resume.domain.repository.CvRepository
import tekin.luetfi.resume.domain.repository.JobAnalyzerRepository
import tekin.luetfi.resume.domain.repository.ModelsRepository
import tekin.luetfi.resume.domain.repository.TechStackInfoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {


    @Provides
    @Singleton
    fun provideApi(@CvApi retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun provideCvRepository(api: Api): CvRepository = DefaultCvRepository(api)

    @Provides
    @Singleton
    fun provideOpenRouterApi(@OpenRouterApi retrofit: Retrofit): OpenRouterAiApi =
        retrofit.create(OpenRouterAiApi::class.java)

    @Provides
    @Singleton
    fun provideJobAnalyzerRepository(
        api: Api,
        openRouterAiApi: OpenRouterAiApi,
        moshi: Moshi,
        db: JobReportDao
    ): JobAnalyzerRepository =
        DefaultJobAnalyzerRepository(
            api = api,
            openRouterAiApi = openRouterAiApi,
            moshi = moshi,
            db = db
        )

    @Provides
    @Singleton
    fun provideDefaultModelsRepository(api: Api): ModelsRepository = DefaultModelsRepository(api)

    @Provides
    @Singleton
    fun provideDefaultTechInfoRepository(api: Api): TechStackInfoRepository =
        DefaultTechStackInfoRepository(api)




}