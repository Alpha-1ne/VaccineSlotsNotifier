package me.alphaone.vaccinenotifier

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "VC_CHANNEL_ID",
                    "Vaccine Notifier",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "APP_CHANNEL",
                    "Important Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

}