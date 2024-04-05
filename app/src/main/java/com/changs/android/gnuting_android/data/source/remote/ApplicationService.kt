package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ApplicationResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApplicationService {
    @GET("/api/v1/board/applications/receivedstate")
    suspend fun getReceiveApplicationList(): Response<ApplicationResponse>

    @GET("/api/v1/board/applications/applystate")
    suspend fun getApplyApplicationList(): Response<ApplicationResponse>


    @PATCH("/api/v1/board/applications/refuse/{id}")
    suspend fun patchRefuse(@Path("id") id: Int): Response<DefaultResponse>

    @POST("/api/v1/board/applications/accept/{id}")
    suspend fun postAccept(@Path("id") id: Int): Response<DefaultResponse>

    @DELETE("/api/v1/board/applications/cancel/{id}")
    suspend fun deleteCancel(@Path("id") id: Int): Response<DefaultResponse>

}