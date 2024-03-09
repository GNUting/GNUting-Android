package com.changs.android.gnuting_android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.model.PostSearchResponse
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.model.SaveResponse
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import com.changs.android.gnuting_android.data.source.PostListPagingSource
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Query

@OptIn(androidx.paging.ExperimentalPagingApi::class)
class PostRepository(retrofit: Retrofit) {
    private val service = retrofit.create(PostInterface::class.java)

    suspend fun getPostList(page: Int = 1) = service.getPostList(page)

    suspend fun getPostDetail(id: Int) = service.getPostDetail(id)

    suspend fun getUserSearch(nickname: String) = service.getUserSearch(nickname)

    suspend fun postSave(saveRequest: SaveRequest) = service.postSave(saveRequest)

    suspend fun patchPostDetail(id: Int, saveRequest: SaveRequest) =
        service.patchPostDetail(id, saveRequest)

    suspend fun getMyPostList(page: Int = 1) = service.getMyPostList(page)

    suspend fun deletePost(id: Int) = service.deletePost(id)

    suspend fun getSearchPost(keyword: String) = service.getSearchPost(keyword)

    suspend fun postApply(id: Int, inUser: List<InUser>) = service.postApply(id, inUser)

    fun getPostListPagingData(): Flow<PagingData<PostResult>> {
        return Pager(PagingConfig(pageSize = 20)) {
            PostListPagingSource(Dispatchers.IO, service)
        }.flow
    }
}