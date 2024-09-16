package com.changs.android.gnuting_android.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.data.source.remote.MemoService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber


class MemoListPagingSource(
    private val ioDispatcher: CoroutineDispatcher, private val service: MemoService
) : PagingSource<Int, MemoResult>() {

    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, MemoResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemoResult> {
        return try {
            val page = params.key ?: 1

            val response = withContext(ioDispatcher) {
                service.getMemoList(page)
            }

            val memoList = response.body()?.result ?: listOf()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (memoList.isEmpty() || (response.body()?.result?.size
                    ?: 0) < 20
            ) null else page + 1

            LoadResult.Page(
                data = memoList, prevKey = prevKey, nextKey = nextKey
            )

        } catch (exception: Exception) {
            Timber.e(exception.message ?: "paging error")
            return LoadResult.Error(exception)
        }
    }
}