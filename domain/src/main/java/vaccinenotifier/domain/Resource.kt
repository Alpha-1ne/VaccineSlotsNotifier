package vaccinenotifier.domain

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Resource<out T>

data class Success<out T>(val data: T) : Resource<T>()

data class Failure(val failureType: FailureType) : Resource<Nothing>()

data class Loading<out T>(val cachedData: T? = null) : Resource<T>()


enum class FailureType {
    FATAL,
    RETRY,
    FORBIDDEN
}

