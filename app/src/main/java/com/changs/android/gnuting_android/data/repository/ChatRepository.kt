package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.source.StompChatSource
import com.changs.android.gnuting_android.data.source.remote.ChatInterface
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import com.changs.android.gnuting_android.data.source.remote.UserInterface
import retrofit2.Retrofit
import javax.inject.Inject

class ChatRepository @Inject constructor(private val service: ChatInterface) {
    suspend fun getChatRoomList() = service.getChatRoomList()

    suspend fun getChats(chatRoomId: Int) = service.getChats(chatRoomId)
}
