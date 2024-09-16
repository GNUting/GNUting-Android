package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.MemoListResponse
import com.changs.android.gnuting_android.data.model.MemoRemainingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MemoService {

    @GET("api/v1/memo")
    suspend fun getMemoList(@Query("page") page: Int = 1): Response<MemoListResponse>

    @GET("api/v1/memo/remaining")
    suspend fun getMemoRemainingCount(): Response<MemoRemainingResponse>

}