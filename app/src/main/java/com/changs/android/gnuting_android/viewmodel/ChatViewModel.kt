package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.data.source.StompChatSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private var stompChatSource: StompChatSource? = null
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> get() = _message
    fun connectChatRoom(chatRoomId: Int) {
        stompChatSource = StompChatSource(chatRoomId)
        stompChatSource?.run {
            viewModelScope.launch {
                topic().collectLatest {
                    _message.value = it
                }
            }
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