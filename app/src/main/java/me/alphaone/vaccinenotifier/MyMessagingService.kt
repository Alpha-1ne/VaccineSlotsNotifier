package me.alphaone.vaccinenotifier

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vaccinenotifier.data.R

class MyMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        if (message.data.isNotEmpty()) {
            val title = data[NOTIFICATION_TITLE_KEY]
            val text = data[NOTIFICATION_TEXT_KEY]
            showNotification(title,text)
        } else {
        }
    }


    private fun showNotification(
        messageTitle: String?,
        messageText: String?
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            "APP_CHANNEL"
        )
            .setContentTitle(messageTitle)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentText(messageText)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            104,
            notificationBuilder.build()
        )
    }

    companion object {
        private const val NOTIFICATION_TITLE_KEY = "title"
        private const val NOTIFICATION_TEXT_KEY = "body"
    }
}