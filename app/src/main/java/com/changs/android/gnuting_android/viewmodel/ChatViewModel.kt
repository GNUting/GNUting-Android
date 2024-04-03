package com.changs.android.gnuting_android.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.ChatListResponse
import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.repository.ChatRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.data.source.StompChatSource
import com.changs.android.gnuting_android.ui.adapter.ChatAdapter
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository
) : BaseViewModel() {
    private var stompChatSource: StompChatSource? = null
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> get() = _message
    fun connectChatRoom(chatRoomId: Int) {
        stompChatSource = StompChatSource(chatRoomId)
        stompChatSource?.run {
            viewModelScope.launch {
                sharedFlow.collectLatest {
                    _message.value = it
                }
            }
            topic()
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

    private val _chatRoomListResponse: MutableLiveData<ChatListResponse> = MutableLiveData()

    val chatRoomListResponse: LiveData<ChatListResponse> get() = _chatRoomListResponse

    fun getChatRoomList() {
        viewModelScope.launch {
            delay(1000)

            try {
                _spinner.value = true
                val response = chatRepository.getChatRoomList()

                handleResult(response = response, handleSuccess = fun() {
                    _chatRoomListResponse.value = response.body()!!
                }) {
                    handleTokenExpiration { getChatRoomList() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

    private val _chatsResponse: MutableLiveData<ChatResponse> = MutableLiveData()

    val chatsResponse: LiveData<ChatResponse> get() = _chatsResponse

    fun getChats(chatRoomId: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = chatRepository.getChats(chatRoomId)

                handleResult(response = response, handleSuccess = fun() {
                    _chatsResponse.value = response.body()!!
                }) {
                    handleTokenExpiration { getChats(chatRoomId) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

}