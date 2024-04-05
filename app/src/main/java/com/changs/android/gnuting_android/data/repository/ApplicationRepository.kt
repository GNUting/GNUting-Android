package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.ApplicationService
import javax.inject.Inject

class ApplicationRepository @Inject constructor(private val service: ApplicationService) {
    suspend fun getReceiveApplicationList() = service.getReceiveApplicationList()

    suspend fun getApplyApplicationList() = service.getApplyApplicationList()

    suspend fun patchRefuse(id: Int) = service.patchRefuse(id)

    suspend fun postAccept(id: Int) = service.postAccept(id)

    suspend fun deleteCancel(id: Int) = service.deleteCancel(id)
}