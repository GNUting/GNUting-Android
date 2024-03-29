package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.AlarmInterface
import retrofit2.Retrofit

class AlarmRepository(retrofit: Retrofit) {
    private val service = retrofit.create(AlarmInterface::class.java)

    suspend fun getAlarmList() = service.getAlarmList()
    suspend fun deleteNotification(id: Int) = service.deleteNotification(id)
}