package com.example.cnchat.retrofit

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.example.cnchat.constants
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object retrofitClient{

    fun getClient(): OkHttpClient? {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        //okhttp3.Interceptor {
        //            val original = it.request()
        //            val requestBuilder = original.newBuilder()
        //            requestBuilder.addHeader("content-type", "application/json")
        //            requestBuilder.addHeader("token", constants.token)
        //            val request = requestBuilder.build()
        //            it.proceed(request)
        //        }

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
        .build()
//        .client(getClient())


    val retrofitService = retrofit.create(apiService::class.java)

}