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
class ChatViewModel @Inject constructor(
    private val userRepository: UserRepository, private val chatRepository: ChatRepository
) : ViewModel() {
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

    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner


    fun onSnackbarShown() {
        _snackbar.value = null
    }

    private val _expirationToken = MutableLiveData<Event<Boolean>>()

    val expirationToken: LiveData<Event<Boolean>>
        get() = _expirationToken

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
                _snackbar.value = "네트워크 에러가 발생했습니다."
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
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    private suspend fun <T : Any> handleResult(
        response: Response<T>,
        handleSuccess: () -> Unit,
        handleError: (() -> Unit)? = null,
        handleTokenExpiration: suspend () -> Unit
    ) {
        if (response.isSuccessful && response.body() != null) {
            handleSuccess()
            _spinner.value = false
        } else {
            response.errorBody()?.let { errorBody ->
                val error = getErrorResponse(errorBody)
                error?.let {
                    _spinner.value = false
                    when {
                        it.code == "BOARD5003" -> {
                            // Handle specific error
                        }

                        it.code == "TOKEN4001-1" -> {
                            handleTokenExpiration()
                        }

                        it.code != null && it.code.contains("TOKEN4001") -> {
                            _expirationToken.value = Event(true)
                        }

                        else -> {
                            handleError?.let { handle ->
                                handle()
                            }
                            _snackbar.value = "네트워크 에러가 발생했습니다."
                        }
                    }
                }
            }
        }
    }


    private suspend fun handleTokenExpiration(reCallApi: () -> Unit) {
        GNUApplication.sharedPreferences.edit().putString(Constant.X_ACCESS_TOKEN, null).apply()
        val refreshToken =
            GNUApplication.sharedPreferences.getString(Constant.X_REFRESH_TOKEN, null)
        if (refreshToken != null) {
            val response = userRepository.postReIssueAccessToken(
                RefreshTokenRequest(refreshToken)
            )
            if (response.isSuccessful && response.body() != null) {
                val accessToken = response.body()!!.result.accessToken
                GNUApplication.sharedPreferences.edit()
                    .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()

                reCallApi()
            } else {
                _expirationToken.value = Event(true)
            }
        } else {
            _expirationToken.value = Event(true)
        }
    }

}