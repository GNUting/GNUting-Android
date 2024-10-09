package com.changs.android.gnuting_android.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.LogoutRequest
import com.changs.android.gnuting_android.data.model.MyInfoResponse
import com.changs.android.gnuting_android.data.model.MyInfoResult
import com.changs.android.gnuting_android.data.model.ProfileResponse
import com.changs.android.gnuting_android.data.model.SaveFCMTokenRequest
import com.changs.android.gnuting_android.data.model.SearchDepartmentResponse
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.data.source.local.TokenManager
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeMainViewModel @Inject constructor(
    private val userRepository: UserRepository, private val tokenManager: TokenManager
) : BaseViewModel() {
    var currentApplicationTab = ApplicationAdapter.ApplicationType.APPLY

    private val myInfoFlow = MutableStateFlow<MyInfoResponse?>(null)

    val myInfo: LiveData<MyInfoResult> = myInfoFlow.flatMapLatest {
        userRepository.myInfoFlow
    }.asLiveData()

    private val _saveFcmTokenResponse = MutableLiveData<Event<Boolean>>()

    val saveFcmTokenResponse: LiveData<Event<Boolean>>
        get() = _saveFcmTokenResponse

    val profileResponse: LiveData<Event<ProfileResponse>>
        get() = _profileResponse

    private val _profileResponse = MutableLiveData<Event<ProfileResponse>>()


    init {
        myInfoFlow.onEach {
            userRepository.fetchRecentMyInfo()
        }.catch { throwable ->
            Timber.e(throwable.message ?: "network error")
        }.launchIn(viewModelScope)
    }

    val images = listOf(
        "https://image.lawtimes.co.kr/images/192523.jpg",
        "https://thumb.mtstarnews.com/06/2023/06/2023062215005684112_1.jpg",
        "https://i.ytimg.com/vi/tbg3QAu-GnI/maxresdefault.jpg",
    )

    var currentItem = 0

    private val _logoutResponse = MutableLiveData<Event<DefaultResponse>>()

    val logoutResponse: LiveData<Event<DefaultResponse>> get() = _logoutResponse

    private val _withdrawalResponse = MutableLiveData<Event<DefaultResponse>>()

    val withdrawalResponse: LiveData<Event<DefaultResponse>> get() = _withdrawalResponse

    private val _searchDepartmentResponse = MutableLiveData<SearchDepartmentResponse>()

    val searchDepartmentResponse: LiveData<SearchDepartmentResponse> get() = _searchDepartmentResponse

    val choiceDepartment = MutableLiveData<String>()

    val nickNameCheck = MutableLiveData<Boolean?>()
    var profileImage: Bitmap? = null

    fun fetchRecentMyInfo() {
        viewModelScope.launch {
            try {
                userRepository.fetchRecentMyInfo()
            } catch (e: Exception) {
                Timber.d("error ${e.message}")
            }
        }
    }

    fun getCheckNickName(inputNickName: String?) {
        viewModelScope.launch {
            inputNickName?.let {
                try {
                    _spinner.value = true
                    val result = userRepository.getCheckNickName(it)
                    if (result.isSuccessful && result.body() != null) {
                        nickNameCheck.value = result.body()!!.result
                        _toast.value = Event(result.body()!!.message)
                        _spinner.value = false
                    } else {
                        result.errorBody()?.let {
                            nickNameCheck.value = false
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    nickNameCheck.value = false
                    _spinner.value = false
                    Timber.e(e.message ?: "network error")
                }
            }
        }
    }

    fun postSaveFcmToken(token: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.postSaveFCMToken(SaveFCMTokenRequest(token))

                handleResult(response = response, handleSuccess = fun() {
                    _saveFcmTokenResponse.value = Event(true)
                })
            } catch (e: Exception) {
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun updateProfile(department: String?, nickname: String, userSelfIntroduction: String?) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = userRepository.patchProfile(
                    department = department,
                    nickname = nickname,
                    profileImage = profileImage,
                    userSelfIntroduction = userSelfIntroduction
                )

                handleResult(response = response, handleSuccess = fun() {
                    _profileResponse.value = Event(response.body()!!)
                    _toast.value = Event("프로필 수정이 완료되었습니다.")
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun logoutUser(fcmToken: String) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val refreshToken = tokenManager.getRefreshToken().firstOrNull() ?: ""

                val response = userRepository.postLogout(LogoutRequest(refreshToken, fcmToken))

                handleResult(response = response, handleSuccess = fun() {
                    _logoutResponse.value = Event(response.body()!!)

                    launch { tokenManager.deleteTokens() }

                    myInfo.value?.let {
                        launch { userRepository.deleteUser(it) }
                    }
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun withdrawal() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = userRepository.deleteWithdrawal()

                handleResult(response = response, handleSuccess = fun() {
                    _withdrawalResponse.value = Event(response.body()!!)

                    launch { tokenManager.deleteTokens() }

                    myInfo.value?.let { launch { userRepository.deleteUser(it) } }
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun getSearchDepartment(department: String?) {
        viewModelScope.launch {
            department?.let {
                try {
                    _spinner.value = true
                    val result = userRepository.getSearchDepartment(it)
                    if (result.isSuccessful && result.body() != null) {
                        _searchDepartmentResponse.value = result.body()
                        _spinner.value = false
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    Timber.e(e.message ?: "network error")
                }
            }
        }
    }

    fun getAccessToken() = tokenManager.getAccessToken()
}