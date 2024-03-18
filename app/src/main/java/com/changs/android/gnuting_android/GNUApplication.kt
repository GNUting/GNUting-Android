package com.changs.android.gnuting_android

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.changs.android.gnuting_android.data.repository.ApplicationRepository
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.data.source.local.AppDatabase
import com.changs.android.gnuting_android.util.Constant
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

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var userRepository: UserRepository
        lateinit var postRepository: PostRepository
        lateinit var applicationRepository: ApplicationRepository
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        sharedPreferences = applicationContext.getSharedPreferences("GNU", MODE_PRIVATE)

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
                    builder.addHeader("Authorization", "Bearer $jwtToken")
                }

                proceed(builder.build())
            }
        }

        val client = OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(logger)
            .addNetworkInterceptor(requestInterceptor).build()

        retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()

        val room = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, Constant.DATABASE_NAME
        ).build()

        userRepository = UserRepository(retrofit, room)
        postRepository = PostRepository(retrofit)
        applicationRepository = ApplicationRepository(retrofit)
    }
}