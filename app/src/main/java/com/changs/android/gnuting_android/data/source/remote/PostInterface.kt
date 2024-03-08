package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.PostDetailResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostInterface {

    @GET("api/v1/board")
    suspend fun getPostList(@Query("page") page: Int = 1): Response<PostResponse>

    @GET("api/v1/board/{id}")
    suspend fun getPostDetail(@Path("id") id: Int): Response<PostDetailResponse>
}