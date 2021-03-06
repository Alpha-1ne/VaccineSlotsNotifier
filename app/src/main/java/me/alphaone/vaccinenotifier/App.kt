package me.alphaone.vaccinenotifier

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "VC_CHANNEL_ID",
                    "Vaccine Notifier",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "VC_CHANNEL_ID_DOSE_TWO",
                    "Vaccine Notifier Dose 2",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "APP_CHANNEL",
                    "Important Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "SERVICE_CHANNEL",
                    "Service Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

}