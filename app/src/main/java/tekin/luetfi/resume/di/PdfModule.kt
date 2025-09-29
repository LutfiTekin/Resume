package tekin.luetfi.resume.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tekin.luetfi.resume.data.repository.AndroidPdfRepository
import tekin.luetfi.resume.domain.repository.PdfRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PdfModule {
    @Binds
    @Singleton
    abstract fun bindPdfRepository(impl: AndroidPdfRepository): PdfRepository
}
