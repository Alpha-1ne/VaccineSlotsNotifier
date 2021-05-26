package vaccinenotifier.data

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import vaccinenotifier.data.api.APIService
import vaccinenotifier.data.api.model.AppointmentsResponse
import vaccinenotifier.domain.AppSettings
import vaccinenotifier.domain.ScheduledData
import vaccinenotifier.domain.Success
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class NotifierService: Service(), CoroutineScope {

    @Inject
   lateinit var apiService: APIService
   @Inject
   lateinit var appSettings: AppSettings

   private val handler = Handler(Looper.getMainLooper())

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext get() = mJob + Dispatchers.Main

    private var scheduledData: ScheduledData? = null

    private var checkSlots = Runnable {
        launch {
            networkBoundRequest(
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
                   it
                },
                onMappingFailure = { Log.d("Test", it.toString()) },
                onApiFailure = { Log.d("Test", it) }
            ).collect {
                if(it is Success)
                processData(applicationContext, it.data)
            }
        }
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
        handler.postDelayed(checkSlots,30000)
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
            .setPriority(Notification.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .addAction(R.drawable.ic_baseline_notifications_24, context.getString(R.string.register),
                contentIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            id,
            notificationBuilder.build()
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mJob = Job()
        handler.post(checkSlots)
        val notification: Notification = NotificationCompat.Builder(
            this, "SERVICE_CHANNEL")
            .setContentTitle("Vaccine Slots Notifier")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_syringe)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkSlots)
        launch {
            appSettings.setScheduled(isScheduled = false, dose1 = false, dose2 = false)
        }
    }


    companion object{
        private const val ONGOING_NOTIFICATION_ID = 181
    }

}