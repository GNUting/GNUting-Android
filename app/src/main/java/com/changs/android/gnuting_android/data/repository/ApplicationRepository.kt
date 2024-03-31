package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.source.remote.ApplicationInterface
import com.changs.android.gnuting_android.data.source.remote.UserInterface
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.Path
import javax.inject.Inject

class ApplicationRepository @Inject constructor(private val service: ApplicationInterface) {
    suspend fun getReceiveApplicationList() = service.getReceiveApplicationList()

    suspend fun getApplyApplicationList() = service.getApplyApplicationList()

    suspend fun patchRefuse(id: Int) = service.patchRefuse(id)

    suspend fun postAccept(id: Int) = service.postAccept(id)

    suspend fun deleteCancel(id: Int) = service.deleteCancel(id)
}