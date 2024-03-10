package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ApplicationResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApplicationInterface {
    @GET("/api/v1/board/applications/receivedstate")
    suspend fun getReceiveApplicationList(): Response<ApplicationResponse>

    @GET("/api/v1/board/applications/applystate")
    suspend fun getApplyApplicationList(): Response<ApplicationResponse>


    @PATCH("/api/v1/board/applications/refuse/{id}")
    suspend fun patchRefuse(@Path("id") id: Int): Response<DefaultResponse>

}