package com.changs.android.gnuting_android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GNUApplication : Application() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        var isActiveChatFragment = false
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        sharedPreferences = applicationContext.getSharedPreferences("GNU", MODE_PRIVATE)
    }
}