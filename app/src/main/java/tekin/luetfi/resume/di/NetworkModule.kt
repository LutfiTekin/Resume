package tekin.luetfi.resume.di


import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tekin.luetfi.resume.BuildConfig
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private const val DEFAULT_TIMEOUT = "dt"
private const val LLM_TIMEOUT = "llm_dt"


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named(DEFAULT_TIMEOUT)
    fun provideDefaultTimeOut(): Long = 3000L

    @Provides
    @Singleton
    @Named(LLM_TIMEOUT)
    fun provideLLMTimeOut(): Long = 30000L

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    @CvOkHttp
    fun provideOkHTTPClient(
        @Named(DEFAULT_TIMEOUT) defaultTimeOut: Long,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @OpenRouterOkHttp
    fun provideORAOkHTTPClient(
        @Named(LLM_TIMEOUT) defaultTimeOut: Long,
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .writeTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(defaultTimeOut, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()


    @Provides
    @Singleton
    fun provideOpenRouterAuthInterceptor(@ApplicationContext context: Context): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            //Optional but recommended by OpenRouter
            .header("HTTP-Referer", context.packageName)
            .header("X-Title", "CVDEMO")

        chain.proceed(builder.build())
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun providesMoshiConverter(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    private fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
        moshiConverter: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(moshiConverter)
            .build()
    }


    @Provides
    @Singleton
    @CvApi
    fun provideRetrofit(
        @CvOkHttp okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = retrofit(BuildConfig.CV_BASE_URL, okHttpClient, moshiConverter)

    @Provides
    @Singleton
    @OpenRouterApi
    fun provideRetrofitORA(
        @OpenRouterOkHttp okHttpClient: OkHttpClient, moshiConverter: MoshiConverterFactory
    ): Retrofit = retrofit(BuildConfig.OPENROUTERAI_API, okHttpClient, moshiConverter)

}