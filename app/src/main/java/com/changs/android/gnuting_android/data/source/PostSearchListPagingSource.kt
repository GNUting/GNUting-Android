package com.changs.android.gnuting_android.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class PostSearchListPagingSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val service: PostInterface,
    private val query: String
) : PagingSource<Int, Content>() {

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, Content>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {
        return try {
            val page = params.key ?: 0

            val response = withContext(ioDispatcher) {
                service.getSearchPost(query, page)
            }

            val postList = response.body()?.result?.content?: listOf()

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (postList.isEmpty() || (response.body()?.result?.size ?: 0) < 20) null else page + 1

            LoadResult.Page(
                data = postList,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}