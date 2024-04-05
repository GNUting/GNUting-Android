package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ChatListResponse
import com.changs.android.gnuting_android.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {
    @GET("/api/v1/chatRoom")
    suspend fun getChatRoomList(): Response<ChatListResponse>

    @GET("/api/v1/chatRoom/{chatRoomId}/chats")
    suspend fun getChats(@Path("chatRoomId") chatRoomId: Int): Response<ChatResponse>

}