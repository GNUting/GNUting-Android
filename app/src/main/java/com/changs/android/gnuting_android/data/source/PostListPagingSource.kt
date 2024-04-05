package com.changs.android.gnuting_android.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.source.remote.PostService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber


class PostListPagingSource(
    private val ioDispatcher: CoroutineDispatcher, private val service: PostService
) : PagingSource<Int, PostResult>() {

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, PostResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostResult> {
        return try {
            val page = params.key ?: 1

            val response = withContext(ioDispatcher) {
                service.getPostList(page)
            }
            val postList = response.body()?.result ?: listOf()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (postList.isEmpty() || (response.body()?.result?.size
                    ?: 0) < 20
            ) null else page + 1

            LoadResult.Page(
                data = postList, prevKey = prevKey, nextKey = nextKey
            )

        } catch (exception: Exception) {
            Timber.e(exception.message ?: "paging error")
            return LoadResult.Error(exception)
        }
    }
}