package com.changs.android.gnuting_android.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.model.ReIssueAccessTokenRequest
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class PostListPagingSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val service: PostInterface,
    private val listener: () -> Unit
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
                var result = service.getPostList(page)
                result.errorBody()?.let {
                    val errorBody = getErrorResponse(it)
                    errorBody?.let { error ->
                        if (error.code == "TOKEN4001") {
                            GNUApplication.sharedPreferences.edit().putString(Constant.X_ACCESS_TOKEN, null).apply()

                            val refreshToken = GNUApplication.sharedPreferences.getString(
                                Constant.X_REFRESH_TOKEN, null
                            )

                            if (refreshToken != null) {
                                val response = service.postReIssueAccessToken(
                                    ReIssueAccessTokenRequest(refreshToken)
                                )

                                if (response.isSuccessful && response.body() != null) {
                                    val accessToken = response.body()!!.result.accessToken
                                    GNUApplication.sharedPreferences.edit()
                                        .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                    result = service.getPostList(page)
                                } else {
                                    listener()
                                }
                            } else {
                                listener()
                            }
                        }
                    }
                }

                result
            }


            val postList = response.body()?.result ?: listOf()

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (postList.isEmpty() || (response.body()?.result?.size
                    ?: 0) < 20
            ) null else page + 1

            LoadResult.Page(
                data = postList, prevKey = prevKey, nextKey = nextKey
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}