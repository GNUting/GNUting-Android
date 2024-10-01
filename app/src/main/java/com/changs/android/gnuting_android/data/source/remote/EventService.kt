package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ApplyChatResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.PostEventRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EventService {
    @GET("/api/v1/event/server/check")
    suspend fun getEventCheck(): Response<DefaultResponse>

    @POST("/api/v1/event/participate")
    suspend fun postEvent(@Body request: PostEventRequestBody): Response<ApplyChatResponse>
}