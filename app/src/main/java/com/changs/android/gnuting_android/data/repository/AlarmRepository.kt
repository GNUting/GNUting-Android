package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.AlarmInterface
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val service: AlarmInterface) {
    suspend fun getAlarmList() = service.getAlarmList()
    suspend fun deleteNotification(id: Int) = service.deleteNotification(id)
}