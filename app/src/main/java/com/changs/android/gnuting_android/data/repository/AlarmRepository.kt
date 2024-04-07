package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.NewAlarmResponse
import com.changs.android.gnuting_android.data.source.remote.AlarmService
import retrofit2.Response
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val service: AlarmService) {
    suspend fun getAlarmList() = service.getAlarmList()
    suspend fun deleteNotification(id: Int) = service.deleteNotification(id)
    suspend fun getNewAlarm() = service.getNewAlarm()
}