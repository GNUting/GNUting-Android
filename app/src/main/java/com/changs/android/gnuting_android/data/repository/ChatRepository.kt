package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.ChatInterface
import javax.inject.Inject

class ChatRepository @Inject constructor(private val service: ChatInterface) {
    suspend fun getChatRoomList() = service.getChatRoomList()

    suspend fun getChats(chatRoomId: Int) = service.getChats(chatRoomId)
}
