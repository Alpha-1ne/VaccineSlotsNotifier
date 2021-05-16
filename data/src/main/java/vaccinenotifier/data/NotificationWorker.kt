package vaccinenotifier.data

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import vaccinenotifier.data.api.APIService
import vaccinenotifier.data.api.model.AppointmentsResponse
import vaccinenotifier.domain.AppSettings
import vaccinenotifier.domain.ScheduledData
import java.text.SimpleDateFormat
import java.util.*


@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: APIService,
    private val appSettings: AppSettings
) : CoroutineWorker(appContext, workerParams) {

    private var scheduledData: ScheduledData? = null

    override suspend fun doWork(): Result {

        return doNetworkRequest(
            createRequest = { /*Request has no body */ },
            request = {
                scheduledData = runBlocking {
                    appSettings.getScheduledData()
                }
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                val date = dateFormat.format(Date(System.currentTimeMillis()))
                apiService.getAppointments(scheduledData?.district?.id.toString(), date)
            },
            onSuccess = {
                processData(applicationContext, it)

            },
            onMappingFailure = { Log.d("Test", it.toString()) },
            onApiFailure = { Log.d("Test", it) }
        )
    }

    private fun processData(
        context: Context,
        appointmentsResponse: AppointmentsResponse
    ) {
        var vaccineCountDose1 = 0
        var vaccineCountDose2 = 0
        appointmentsResponse.centers?.filter { center ->
            center.sessions?.any { session -> session.available > 0 && session.ageLimit == 18 }
                ?: false
        }?.forEach {
            it.sessions?.forEach { session ->
                vaccineCountDose1 += session.availableDose1
                vaccineCountDose2 += session.availableDose2
            }
        }
        if (vaccineCountDose1 > 0 && scheduledData?.dose1 == true)
            showNotification(
                context,
                "New Slots Available for First Dose",
                "$vaccineCountDose1 slots available for first dose in your district ${scheduledData?.district?.name}",
                "VC_CHANNEL_ID",
                102
            )
        if (vaccineCountDose2 > 0 && scheduledData?.dose2 == true)
            showNotification(
                context,
                "New Slots Available for Second Dose",
                "$vaccineCountDose2 slots available for second dose in your district ${scheduledData?.district?.name}",
                "VC_CHANNEL_ID_DOSE_TWO",
                103
            )
    }

    private fun showNotification(
        context: Context,
        messageTitle: String?,
        messageText: String?,
        notificationChannel: String,
        id: Int
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://selfregistration.cowin.gov.in/"))
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        val notificationBuilder = NotificationCompat.Builder(
            context, notificationChannel
        )
            .setContentTitle(messageTitle)
            .setSmallIcon(R.drawable.ic_syringe)
            .setContentText(messageText)
            .setAutoCancel(true)
            .setPriority(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_HIGH else Notification.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setContentIntent(contentIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            id,
            notificationBuilder.build()
        )
    }

}