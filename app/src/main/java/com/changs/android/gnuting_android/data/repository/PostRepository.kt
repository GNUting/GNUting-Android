package com.changs.android.gnuting_android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.source.MyPostListPagingSource
import com.changs.android.gnuting_android.data.source.PostListPagingSource
import com.changs.android.gnuting_android.data.source.PostSearchListPagingSource
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit

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

    suspend fun postReport(reportRequest: ReportRequest) = service.postBoardReport(reportRequest)

    fun getPostListPagingData(): Flow<PagingData<PostResult>> {
        return Pager(PagingConfig(pageSize = 20)) {
            PostListPagingSource(Dispatchers.IO, service)
        }.flow
    }

    fun getMyPostListPagingData(): Flow<PagingData<PostResult>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MyPostListPagingSource(Dispatchers.IO, service)
        }.flow
    }

    fun getSearchPostListPagingData(query: String): Flow<PagingData<Content>> {
        return Pager(PagingConfig(pageSize = 20)) {
            PostSearchListPagingSource(Dispatchers.IO, service, query)
        }.flow
    }
}