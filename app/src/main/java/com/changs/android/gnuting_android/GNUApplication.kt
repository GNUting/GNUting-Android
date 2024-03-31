package com.changs.android.gnuting_android

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.changs.android.gnuting_android.data.repository.AlarmRepository
import com.changs.android.gnuting_android.data.repository.ApplicationRepository
import com.changs.android.gnuting_android.data.repository.ChatRepository
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.data.source.local.AppDatabase
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Constant.BASE_URL
import com.changs.android.gnuting_android.util.Constant.X_ACCESS_TOKEN
import dagger.hilt.android.HiltAndroidApp
import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class GNUApplication : Application() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        var isActiveChatFragment: Boolean? = null
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        isActiveChatFragment = false

        sharedPreferences = applicationContext.getSharedPreferences("GNU", MODE_PRIVATE)
    }
}