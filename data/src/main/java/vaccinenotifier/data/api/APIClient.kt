package vaccinenotifier.data.api
import okhttp3.OkHttpClient
import okhttp3.internal.userAgent
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class APIClient {

    var apiService: APIService

    init {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val logger = HttpLoggingInterceptor().apply { level = Level.BODY
        }
            okHttpClientBuilder.addNetworkInterceptor { chain ->
                chain.proceed(chain.request()
                    .newBuilder()
                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .build())
            }.addNetworkInterceptor(logger)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://cdn-api.co-vin.in/api/v2/")
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        apiService = retrofit.create(APIService::class.java)
    }


}

