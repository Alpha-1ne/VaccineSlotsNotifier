package vaccinenotifier.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
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

    private var scheduledData:ScheduledData? = null

    override suspend fun doWork(): Result {

        return doNetworkRequest(
            createRequest = { /*Request has no body */ },
            request = {
                scheduledData = runBlocking {
                    appSettings.getScheduledData()
                }
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                val date = dateFormat.format(Date(System.currentTimeMillis()))
                apiService.getAppointments(scheduledData?.district?.id.toString(),date)
                      },
            onSuccess = {
                processData(applicationContext,it)

            },
            onMappingFailure = {  },
            onApiFailure = {  }
        )
    }

    private fun processData(
        context: Context,
        appointmentsResponse: AppointmentsResponse
    ) {
        var vaccineCount = 0
        appointmentsResponse.centers?.filter { center ->
            center.sessions?.any { session -> session.available>0 && session.ageLimit == 18 }?:false
        }?.forEach {
            it.sessions?.forEach {
                session ->
                vaccineCount+=session.available
            }
        }
        if(vaccineCount>0)
            showNotification(context,"New Slots Available", "$vaccineCount slots available in your district ${scheduledData?.district?.name}")
    }

    private fun showNotification(
        context: Context,
        messageTitle: String?,
        messageText: String?
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://selfregistration.cowin.gov.in/"))
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        val notificationBuilder = NotificationCompat.Builder(
            context,
            "VC_CHANNEL_ID"
        )
            .setContentTitle(messageTitle)
            .setSmallIcon(R.drawable.ic_syringe)
            .setContentText(messageText)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setContentIntent(contentIntent)

        val notificationManager =
           context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            102,
            notificationBuilder.build()
        )
    }

}