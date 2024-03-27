package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.source.remote.ChatInterface
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import retrofit2.Retrofit

class ChatRepository(retrofit: Retrofit) {
    private val service = retrofit.create(ChatInterface::class.java)

    suspend fun getChatRoomList() = service.getChatRoomList()

    suspend fun getChats(chatRoomId: Int) = service.getChats(chatRoomId)
}
