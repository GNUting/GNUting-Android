package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.ChatDetailResponse
import com.changs.android.gnuting_android.data.model.ChatListResponse
import com.changs.android.gnuting_android.data.model.ChatListResponse2
import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.model.ChatRoomUsersResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatService {
    @GET("/api/v1/chatRoom")
    suspend fun getChatRoomList(): Response<ChatListResponse2>

    @GET("/api/v1/chatRoom/{chatRoomId}/chats")
    suspend fun getChats(@Path("chatRoomId") chatRoomId: Int): Response<ChatResponse>

    @POST("/api/v1/chatRoom/{chatRoomId}/leave")
    suspend fun postChatLeave(@Path("chatRoomId") chatRoomId: Int): Response<DefaultResponse>

    @GET("/api/v1/chatRoom/{chatRoomId}/chatRoomUsers")
    suspend fun getChatRoomUsers(@Path("chatRoomId") chatRoomId: Int): Response<ChatRoomUsersResponse>

    @GET("/api/v1/notification/chat/click/{id}")
    suspend fun getChatDetail(@Path("id") id: Int): Response<ChatDetailResponse>
}