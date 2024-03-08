package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Query

class PostRepository(retrofit: Retrofit) {
    private val service = retrofit.create(PostInterface::class.java)

    suspend fun getPostList(page: Int = 1) = service.getPostList(page)

    suspend fun getPostDetail(id: Int) = service.getPostDetail(id)
}