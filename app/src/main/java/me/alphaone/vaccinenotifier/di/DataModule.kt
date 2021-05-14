package me.alphaone.vaccinenotifier.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import vaccinenotifier.data.AppSettingsImpl
import vaccinenotifier.data.VaccineRepositoryImpl
import vaccinenotifier.data.api.APIClient
import vaccinenotifier.data.api.APIService
import vaccinenotifier.data.dataStore
import vaccinenotifier.domain.AppSettings
import vaccinenotifier.domain.VaccineRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideAPIService(): APIService =  APIClient().apiService

    @Provides
    @Singleton
    fun provideAppSettings(@ApplicationContext context: Context): AppSettings {
        return AppSettingsImpl(context.dataStore)
    }
}