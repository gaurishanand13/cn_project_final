package com.example.cnchat.retrofit
import android.content.Context
import com.example.cnchat.constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object retrofitClient{

    class AuthInterceptor(context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader("Authorization", "Bearer ${constants.token}")
            return chain.proceed(requestBuilder.build())
        }
    }
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.writeTimeout(60, TimeUnit.SECONDS)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.retryOnConnectionFailure(true)
        httpClient.addInterceptor(interceptor)
        val client = httpClient.build()
        return client
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(constants.baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getClient())
        .build()


    val retrofitService = retrofit.create(apiService::class.java)

}