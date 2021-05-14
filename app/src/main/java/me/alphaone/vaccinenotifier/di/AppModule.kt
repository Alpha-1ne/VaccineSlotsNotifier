package me.alphaone.vaccinenotifier.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vaccinenotifier.data.AppSettingsImpl
import vaccinenotifier.data.VaccineRepositoryImpl
import vaccinenotifier.domain.AppSettings
import vaccinenotifier.domain.VaccineRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Singleton
    @Binds
    abstract fun bindPaymentRepository(
        paymentRepository: VaccineRepositoryImpl
    ): VaccineRepository

}
