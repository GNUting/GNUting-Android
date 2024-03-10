package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.ApplicationInterface
import retrofit2.Retrofit

class ApplicationRepository(retrofit: Retrofit) {
    private val service = retrofit.create(ApplicationInterface::class.java)

    suspend fun getReceiveApplicationList() = service.getReceiveApplicationList()

    suspend fun getApplyApplicationList( )= service.getApplyApplicationList()

    suspend fun patchRefuse(id: Int) = service.patchRefuse(id)
}