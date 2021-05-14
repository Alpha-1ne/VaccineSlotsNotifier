package vaccinenotifier.data

import kotlinx.coroutines.flow.*
import retrofit2.Response
import vaccinenotifier.domain.Failure
import vaccinenotifier.domain.FailureType
import vaccinenotifier.domain.Loading
import vaccinenotifier.domain.Success
import java.io.IOException


inline fun <ResultType, ResponseType, EntityType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Response<ResponseType>,
    crossinline map: suspend (ResponseType) -> EntityType,
    crossinline save: suspend (EntityType) -> Unit,
    crossinline shouldFetch: suspend () -> Boolean,
    crossinline onMappingFailure: (String?) -> Unit = { },
    crossinline onApiFailure: (String) -> Unit = { }
) = flow {
    // Once query is used here for cached data, make sure nullable Flow<T?> type for
    // all database queries is used, which can be null and make [query: () -> Flow<ResultType?>,] above
    emit(Loading(null))

    val flow = if (shouldFetch()) {

        try {
            val response = fetch()
            when (response.code()) {
                200 -> {
                    save(map(response.body()!!))
                    query().distinctUntilChanged().map { Success(it) }
                }
                401, 403 -> {
                    onApiFailure("Access unauthorized or forbidden with status code ${response.code()}.")
                    query().map { Failure(FailureType.FORBIDDEN) }
                }
                else -> {
                    onApiFailure("Error with status code ${response.code()}.")
                    query().map { Failure(FailureType.FATAL) }
                }
            }
        } catch (ioException: IOException) {
            // IOException will only propagated to the UI to show retry option
            query().map { Failure(FailureType.RETRY) }
        } catch (exception: Exception) {
            onMappingFailure(exception.message)
            query().map { Failure(FailureType.FATAL) }
        }
    } else {
        query().distinctUntilChanged().map { Success(it) }
    }

    emitAll(flow)
}