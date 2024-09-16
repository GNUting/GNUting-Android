package com.changs.android.gnuting_android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.data.model.PostMemoRequestBody
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.source.MemoListPagingSource
import com.changs.android.gnuting_android.data.source.remote.MemoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(androidx.paging.ExperimentalPagingApi::class)
class MemoRepository @Inject constructor(private val service: MemoService) {
    suspend fun postApplyMemo(id: Int) = service.postMemoApply(id)

    suspend fun postSaveMemo(request: PostMemoRequestBody) = service.postMemoSave(request)

    suspend fun getMemoRemainingCount() = service.getMemoRemainingCount()

    fun getMemoListPagingData(): Flow<PagingData<MemoResult>> {
        return Pager(PagingConfig(pageSize = 20)) {
            MemoListPagingSource(Dispatchers.IO, service)
        }.flow
    }
}