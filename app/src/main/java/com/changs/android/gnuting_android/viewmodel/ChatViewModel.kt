package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.ChatListResponse
import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.model.ChatRoomUsersResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.repository.ChatRepository
import com.changs.android.gnuting_android.data.source.StompChatSource
import com.changs.android.gnuting_android.data.source.local.TokenManager
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository, private val tokenManager: TokenManager
) : BaseViewModel() {
    private var stompChatSource: StompChatSource? = null
    private val _message: MutableStateFlow<String?> = MutableStateFlow(null)
    val message: StateFlow<String?> get() = _message

    fun connectChatRoom(chatRoomId: Int) {
        stompChatSource = StompChatSource(chatRoomId, tokenManager)
        stompChatSource?.run {
            viewModelScope.launch {
                sharedFlow.collect {
                    _message.emit(it)
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
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
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
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    private val _chatRoomLeaveResponse: MutableLiveData<Event<DefaultResponse>> = MutableLiveData()

    val chatRoomLeaveResponse: LiveData<Event<DefaultResponse>> get() = _chatRoomLeaveResponse

    fun chatRoomLeave(chatRoomId: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = chatRepository.postChatLeave(chatRoomId)

                handleResult(response = response, handleSuccess = fun() {
                    _chatRoomLeaveResponse.value = Event(response.body()!!)
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    private val _chatRoomUsersResponse: MutableLiveData<ChatRoomUsersResponse> = MutableLiveData()

    val chatRoomUsersResponse: LiveData<ChatRoomUsersResponse> get() = _chatRoomUsersResponse

    fun getChatRoomUsers(chatRoomId: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = chatRepository.getChatRoomUsers(chatRoomId)

                handleResult(response = response, handleSuccess = fun() {
                    _chatRoomUsersResponse.value = response.body()!!
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }
}