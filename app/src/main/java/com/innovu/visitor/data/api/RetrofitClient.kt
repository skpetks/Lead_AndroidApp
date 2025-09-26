package com.innovu.visitor.data.api

import com.google.gson.GsonBuilder
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.utlis.StorePrefData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    private const val BASE_URL  = BuildConfig.SERVICE_END_POINT;

    var TIMEOUT: Long = 60000
    var httpClient = OkHttpClient.Builder()
        .readTimeout(TIMEOUT, TimeUnit.MICROSECONDS)
        .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .callTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)

    private val interceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    var gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) // ⏱️ Connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // ⏱️ Read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // ⏱️ W
        .addNetworkInterceptor(interceptor)
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
//        .addHeader("Authorization", AUTH)
                .addHeader("Content-Type", "application/json")
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    val instance: ApiService by lazy{
        val retrofit = Retrofit.Builder()

            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }






}