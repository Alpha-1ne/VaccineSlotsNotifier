package vaccinenotifier.data

import android.content.Context
import androidx.work.*
import androidx.work.ListenableWorker.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

internal suspend inline fun <ResultType, ResponseType, RequestType> CoroutineWorker.doNetworkRequest(
    crossinline createRequest: suspend () -> RequestType,
    crossinline request: suspend (RequestType) -> Response<ResponseType>,
    crossinline onSuccess: suspend (ResponseType) -> ResultType,
    crossinline onMappingFailure: (String?) -> Unit = { },
    crossinline onApiFailure: (String) -> Unit = { }
): Result {
    try {
        val response = request(createRequest())
        return when (response.code()) {
            200 -> {
                onSuccess(response.body()!!)
                Result.success()
            }
            401, 403 -> {
                onApiFailure("Access unauthorized or forbidden with status code ${response.code()}.")
                Result.failure()
            }
            else -> {
                onApiFailure("Error with status code ${response.code()}.")
                Result.failure()
            }
        }
    } catch (ioException: IOException) {
        return Result.retry()
    } catch (exception: Exception) {
        onMappingFailure(exception.message)
        return Result.failure()
    }


}
const val workerUniqueID = "notify_worker"

fun scheduleWork(appContext: Context) {
    val constraints =
        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val builder = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
    WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(workerUniqueID,ExistingPeriodicWorkPolicy.REPLACE,builder.build())
}

fun stopWork(appContext: Context){
    WorkManager.getInstance(appContext).cancelUniqueWork(workerUniqueID)
}


