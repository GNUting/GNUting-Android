package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.data.source.StompChatSource
class ChatViewModel : ViewModel() {
    private var stompChatSource: StompChatSource? = null

    fun connectChatRoom(chatRoomId: Int, updateMessage: (String) -> Unit) {
        stompChatSource = StompChatSource(chatRoomId)
        stompChatSource?.run {
            topic(updateMessage)
            connect()
            setLifecycleSubscribe()
        }
    }

    fun disConnectChatRoom() {
        stompChatSource?.disConnect()
    }

    fun sendMessage(message: String) {
        stompChatSource?.sendMessage(message)
    }
}