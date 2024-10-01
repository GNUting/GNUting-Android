package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ApplyChatResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.MemoListResponse
import com.changs.android.gnuting_android.data.model.MemoRemainingResponse
import com.changs.android.gnuting_android.data.model.PostMemoRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MemoService {

    @GET("api/v1/memo")
    suspend fun getMemoList(@Query("page") page: Int = 1): Response<MemoListResponse>

    @GET("api/v1/memo/remaining")
    suspend fun getMemoRemainingCount(): Response<MemoRemainingResponse>

    @POST("api/v1/memo/{id}")
    suspend fun postMemoApply(@Path("id") id: Int): Response<ApplyChatResponse>

    @POST("api/v1/memo/save")
    suspend fun postMemoSave(@Body request: PostMemoRequestBody): Response<DefaultResponse>
}