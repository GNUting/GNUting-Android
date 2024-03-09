package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.ApplicationResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.model.SaveResponse
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import com.changs.android.gnuting_android.data.source.remote.ApplicationInterface
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class ApplicationRepository(retrofit: Retrofit) {
    private val service = retrofit.create(ApplicationInterface::class.java)

    suspend fun getReceiveApplicationList() = service.getReceiveApplicationList()

    suspend fun getApplyApplicationList( )= service.getApplyApplicationList()

    suspend fun patchRefuse(id: Int) = service.patchRefuse(id)
}