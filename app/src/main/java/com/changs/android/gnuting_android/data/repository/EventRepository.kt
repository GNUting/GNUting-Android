package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.PostEventRequestBody
import com.changs.android.gnuting_android.data.source.remote.EventService
import javax.inject.Inject

@OptIn(androidx.paging.ExperimentalPagingApi::class)
class EventRepository @Inject constructor(private val service: EventService) {
    suspend fun postEvent(request: PostEventRequestBody) = service.postEvent(request)

    suspend fun getEventCheck() = service.getEventCheck()
}