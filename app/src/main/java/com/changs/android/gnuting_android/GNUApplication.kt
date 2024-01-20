package com.changs.android.gnuting_android

import android.app.Application
import com.changs.android.gnuting_android.util.Constant.BASE_URL
import com.changs.android.gnuting_android.util.Constant.X_ACCESS_TOKEN
import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GNUApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val sharedPreferences = applicationContext.getSharedPreferences("GNU", MODE_PRIVATE)

        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val requestInterceptor = Interceptor { chain ->
            with(chain) {
                val builder: Request.Builder = chain.request().newBuilder()
                val jwtToken: String? = sharedPreferences.getString(X_ACCESS_TOKEN, null)
                if (jwtToken != null) {
                    builder.addHeader(X_ACCESS_TOKEN, jwtToken)
                }

                proceed(builder.build())
            }
        }

        val client = OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(logger)
            .addNetworkInterceptor(requestInterceptor).build()

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

    }
}