package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.NewAlarmResponse
import com.changs.android.gnuting_android.data.model.NotificationSettingRequest
import com.changs.android.gnuting_android.data.source.remote.AlarmService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val service: AlarmService) {
    suspend fun getAlarmList() = service.getAlarmList()

    suspend fun deleteNotification(id: Int) = service.deleteNotification(id)

    suspend fun getNewAlarm() = service.getNewAlarm()

    suspend fun getOverallNotificationStatus() = service.getOverallNotificationStatus()

    suspend fun getCurrentChatRoomNotificationStatus(chatRoomId: Int) = service.getCurrentChatRoomNotificationStatus(chatRoomId)

    suspend fun putOverallNotificationStatus(notificationSettingRequest: NotificationSettingRequest) = service.putOverallNotificationStatus(notificationSettingRequest)

    suspend fun putCurrentChatRoomNotificationStatus(chatRoomId: Int, notificationSettingRequest: NotificationSettingRequest) = service.putCurrentChatRoomNotificationStatus(chatRoomId, notificationSettingRequest)
}