package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.AlarmListResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.NewAlarmResponse
import com.changs.android.gnuting_android.data.model.NotificationSettingRequest
import com.changs.android.gnuting_android.data.model.NotificationSettingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlarmService {
    @GET("/api/v1/notification")
    suspend fun getAlarmList(): Response<AlarmListResponse>

    @DELETE("/api/v1/notification/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): Response<DefaultResponse>

    @GET("/api/v1/notification/check")
    suspend fun getNewAlarm(): Response<NewAlarmResponse>

    @GET("/api/v1/notification/show/allsetting")
    suspend fun getOverallNotificationStatus(): Response<NotificationSettingResponse>

    @GET("api/v1/{chatRoomId}/show/notificationSetting")
    suspend fun getCurrentChatRoomNotificationStatus(@Path("chatRoomId") chatRoomId: Int): Response<NotificationSettingResponse>

    @PUT("/api/v1/notificationSetting")
    suspend fun putOverallNotificationStatus(@Body notificationSettingRequest: NotificationSettingRequest): Response<DefaultResponse>

    @PUT("/api/v1/{chatRoomId}/notificationSetting")
    suspend fun putCurrentChatRoomNotificationStatus(@Path("chatRoomId") chatRoomId: Int, @Body notificationSettingRequest: NotificationSettingRequest): Response<DefaultResponse>
}