package vaccinenotifier.data


import vaccinenotifier.domain.Failure
import vaccinenotifier.domain.FailureType
import vaccinenotifier.domain.Loading
import vaccinenotifier.domain.Success
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException


inline fun <ResultType, ResponseType, RequestType> networkBoundRequest(
    crossinline createRequest: suspend () -> RequestType,
    crossinline request: suspend (RequestType) -> Response<ResponseType>,
    crossinline onSuccess: suspend (ResponseType) -> ResultType,
    crossinline onMappingFailure: (String?) -> Unit = { },
    crossinline onApiFailure: (String) -> Unit = { }
) = flow {
    emit(Loading())
    try {
        val response = request(createRequest())
        val result = when (response.code()) {
            200 -> {
                Success(onSuccess(response.body()!!))
            }
            401, 403 -> {
                onApiFailure("Access unauthorized or forbidden with status code ${response.code()}.")
                Failure(FailureType.FORBIDDEN)
            }
            else -> {
                onApiFailure("Error with status code ${response.code()}.")
                Failure(FailureType.FATAL)
            }
        }
        emit(result)
    } catch (ioException: IOException) {
        // IOException will only propagated to the UI to show retry option
        emit(Failure(FailureType.RETRY))
    } catch (exception: Exception) {
        onMappingFailure(exception.message)
        emit(Failure(FailureType.FATAL))
    }
}