package com.changs.android.gnuting_android.data.repository

import com.changs.android.gnuting_android.data.source.remote.ChatService
import javax.inject.Inject

class ChatRepository @Inject constructor(private val service: ChatService) {
    suspend fun getChatRoomList() = service.getChatRoomList()

    suspend fun getChats(chatRoomId: Int) = service.getChats(chatRoomId)

    suspend fun postChatLeave(chatRoomId: Int) = service.postChatLeave(chatRoomId)

    suspend fun getChatRoomUsers(chatRoomId: Int) = service.getChatRoomUsers(chatRoomId)
}
