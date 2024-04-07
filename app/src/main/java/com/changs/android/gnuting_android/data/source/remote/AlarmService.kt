package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.AlarmListResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.NewAlarmResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface AlarmService {
    @GET("/api/v1/notification")
    suspend fun getAlarmList(): Response<AlarmListResponse>

    @DELETE("/api/v1/notification/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): Response<DefaultResponse>

    @GET("/api/v1/notification/check")
    suspend fun getNewAlarm(): Response<NewAlarmResponse>
}