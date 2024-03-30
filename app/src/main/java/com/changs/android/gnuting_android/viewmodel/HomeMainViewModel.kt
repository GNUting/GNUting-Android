package com.changs.android.gnuting_android.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.AlarmListResponse
import com.changs.android.gnuting_android.data.model.ApplicationResponse
import com.changs.android.gnuting_android.data.model.ChatListResponse
import com.changs.android.gnuting_android.data.model.ChatResponse
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.MyInfoResponse
import com.changs.android.gnuting_android.data.model.MyInfoResult
import com.changs.android.gnuting_android.data.model.PostDetailResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.ProfileResponse
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.SaveFCMTokenRequest
import com.changs.android.gnuting_android.data.model.SearchDepartmentResponse
import com.changs.android.gnuting_android.data.model.UserReportRequest
import com.changs.android.gnuting_android.data.repository.AlarmRepository
import com.changs.android.gnuting_android.data.repository.ApplicationRepository
import com.changs.android.gnuting_android.data.repository.ChatRepository
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import kotlin.Exception

@ExperimentalCoroutinesApi
class HomeMainViewModel(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val applicationRepository: ApplicationRepository,
    private val chatRepository: ChatRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val myInfoFlow = MutableStateFlow<MyInfoResponse?>(null)

    val myInfo: LiveData<MyInfoResult> = myInfoFlow.flatMapLatest {
        userRepository.myInfoFlow
    }.asLiveData()

    private val _saveResponse = MutableLiveData<Event<DefaultResponse>>()

    val saveResponse: LiveData<Event<DefaultResponse>>
        get() = _saveResponse

    private val _applyChatResponse = MutableLiveData<Event<DefaultResponse>>()

    val applyChatResponse: LiveData<Event<DefaultResponse>>
        get() = _applyChatResponse

    private val _patchPostDetailResponse = MutableLiveData<Event<DefaultResponse>>()

    val patchPostDetailResponse: LiveData<Event<DefaultResponse>>
        get() = _patchPostDetailResponse

    private val _refuseResponse = MutableLiveData<Event<DefaultResponse>>()

    val refuseResponse: LiveData<Event<DefaultResponse>>
        get() = _refuseResponse

    private val _acceptResponse = MutableLiveData<Event<DefaultResponse>>()

    val acceptResponse: LiveData<Event<DefaultResponse>>
        get() = _acceptResponse

    private val _cancelResponse = MutableLiveData<Event<DefaultResponse>>()

    val cancelResponse: LiveData<Event<DefaultResponse>>
        get() = _cancelResponse

    private val _reportResponse = MutableLiveData<Event<DefaultResponse>>()

    val reportResponse: LiveData<Event<DefaultResponse>>
        get() = _reportResponse

    private val _userReportResponse = MutableLiveData<Event<DefaultResponse>>()

    val userReportResponse: LiveData<Event<DefaultResponse>>
        get() = _userReportResponse

    private val _expirationToken = MutableLiveData<Event<Boolean>>()

    val expirationToken: LiveData<Event<Boolean>>
        get() = _expirationToken

    private val _saveFcmTokenResponse = MutableLiveData<Event<Boolean>>()

    val saveFcmTokenResponse: LiveData<Event<Boolean>>
        get() = _saveFcmTokenResponse

    val profileResponse: LiveData<Event<ProfileResponse>>
        get() = _profileResponse

    private val _profileResponse = MutableLiveData<Event<ProfileResponse>>()


    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner


    fun onSnackbarShown() {
        _snackbar.value = null
    }

    init {
        myInfoFlow.onEach {
            _spinner.value = true
            userRepository.fetchRecentMyInfo()
        }.onEach {
            _spinner.value = false
        }.catch { throwable ->
            _spinner.value = false
        }.launchIn(viewModelScope)
    }

    val images = listOf(
        "https://image.lawtimes.co.kr/images/192523.jpg",
        "https://thumb.mtstarnews.com/06/2023/06/2023062215005684112_1.jpg",
        "https://i.ytimg.com/vi/tbg3QAu-GnI/maxresdefault.jpg",
    )

    var currentItem = 0

    private val _postResponse = MutableLiveData<PostResponse>()

    val postResponse: LiveData<PostResponse> get() = _postResponse

    private val _myPostResponse = MutableLiveData<PostResponse>()

    val myPostResponse: LiveData<PostResponse> get() = _myPostResponse

    private val _postDetailResponse = MutableLiveData<PostDetailResponse>()

    val postDetailResponse: LiveData<PostDetailResponse> get() = _postDetailResponse

    private val _deletePostResponse = MutableLiveData<Event<DefaultResponse>>()

    val deletePostResponse: LiveData<Event<DefaultResponse>> get() = _deletePostResponse

    private val _logoutResponse = MutableLiveData<Event<DefaultResponse>>()

    val logoutResponse: LiveData<Event<DefaultResponse>> get() = _logoutResponse

    private val _withdrawalResponse = MutableLiveData<Event<DefaultResponse>>()

    val withdrawalResponse: LiveData<Event<DefaultResponse>> get() = _withdrawalResponse


    private val _applicationApplyStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationApplyStateResponse: LiveData<ApplicationResponse> get() = _applicationApplyStateResponse

    private val _applicationReceiveStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationReceiveStateResponse: LiveData<ApplicationResponse> get() = _applicationReceiveStateResponse

    private val _searchDepartmentResponse = MutableLiveData<SearchDepartmentResponse>()

    val searchDepartmentResponse: LiveData<SearchDepartmentResponse> get() = _searchDepartmentResponse

    val choiceDepartment = MutableLiveData<String>()

    val nickNameCheck = MutableLiveData<Boolean>()

    private val _chatRoomListResponse: MutableLiveData<ChatListResponse> = MutableLiveData()

    val chatRoomListResponse: LiveData<ChatListResponse> get() = _chatRoomListResponse

    private val _chatsResponse: MutableLiveData<ChatResponse> = MutableLiveData()

    val chatsResponse: LiveData<ChatResponse> get() = _chatsResponse

    private val _deleteAlarmResponse = MutableLiveData<Event<DefaultResponse>>()

    val deleteAlarmResponse: LiveData<Event<DefaultResponse>> get() = _deleteAlarmResponse

    private val _alarmListResponse = MutableLiveData<AlarmListResponse>()

    val alarmListResponse: LiveData<AlarmListResponse> get() = _alarmListResponse

    var department: String? = null
    var nickname: String? = null
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
                        nickname = it
                        _snackbar.value = result.body()!!.message
                        _spinner.value = false
                    } else {
                        result.errorBody()?.let {
                            nickNameCheck.value = false
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _snackbar.value = error.message
                            }
                        }
                    }
                } catch (e: Exception) {
                    nickNameCheck.value = false
                    _spinner.value = false
                    _snackbar.value = "네트워크 에러가 발생했습니다."
                }
            }
        }
    }

    fun getPostPagingList(): Flow<PagingData<PostResult>> {
        return postRepository.getPostListPagingData(::pagingSourceListener).cachedIn(viewModelScope)
    }

    fun getMyPostPagingList(): Flow<PagingData<PostResult>> {
        return postRepository.getMyPostListPagingData(::pagingSourceListener)
            .cachedIn(viewModelScope)
    }

    fun getSearchPostPagingList(query: String): Flow<PagingData<Content>> {
        return postRepository.getSearchPostListPagingData(query, ::pagingSourceListener)
            .cachedIn(viewModelScope)
    }


    fun getApplicationReceiveList() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = applicationRepository.getReceiveApplicationList()

                handleResult(response = response, handleSuccess = fun() {
                    _applicationReceiveStateResponse.value = response.body()
                }) {
                    handleTokenExpiration { getApplicationReceiveList() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun getApplicationApplyList() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = applicationRepository.getApplyApplicationList()

                handleResult(response = response, handleSuccess = fun() {
                    _applicationApplyStateResponse.value = response.body()
                }) {
                    handleTokenExpiration { getApplicationApplyList() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }


    fun getPostList(page: Int = 1) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.getPostList(page)

                handleResult(response = response, handleSuccess = fun() {
                    _postResponse.value = response.body()
                }) {
                    handleTokenExpiration { getPostList(page) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }


    fun getPostDetail(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.getPostDetail(id)

                handleResult(response = response, handleSuccess = fun() {
                    _postDetailResponse.value = response.body()
                }) {
                    handleTokenExpiration { getPostDetail(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun postSave(saveRequest: SaveRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.postSave(saveRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _saveResponse.value = Event(response.body()!!)
                    _snackbar.value = "게시물 작성이 완료되었습니다."
                }) {
                    handleTokenExpiration { postSave(saveRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun refuse(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = applicationRepository.patchRefuse(id)

                handleResult(response = response, handleSuccess = fun() {
                    _refuseResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { refuse(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun accept(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = applicationRepository.postAccept(id)

                handleResult(response = response, handleSuccess = fun() {
                    _acceptResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { accept(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun cancel(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = applicationRepository.deleteCancel(id)

                handleResult(response = response, handleSuccess = fun() {
                    _cancelResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { cancel(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun report(reportRequest: ReportRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.postReport(reportRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _reportResponse.value = Event(response.body()!!)
                    _snackbar.value = response.body()!!.result
                }) {
                    handleTokenExpiration { report(reportRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun userReport(reportRequest: UserReportRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = userRepository.postUserReport(reportRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _userReportResponse.value = Event(response.body()!!)
                    _snackbar.value = response.body()!!.result
                }) {
                    handleTokenExpiration { userReport(reportRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun patchSave(id: Int, saveRequest: SaveRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.patchPostDetail(id, saveRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _patchPostDetailResponse.value = Event(response.body()!!)
                    _snackbar.value = "게시물 수정이 완료되었습니다."
                }) {
                    handleTokenExpiration { patchSave(id, saveRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun deletePost(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.deletePost(id)

                handleResult(response = response, handleSuccess = fun() {
                    _deletePostResponse.value = Event(response.body()!!)
                    _snackbar.value = "게시물 삭제가 완료되었습니다."
                }) {
                    handleTokenExpiration { deletePost(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun postApplyChat(id: Int, inUser: List<InUser>) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.postApply(id, inUser)

                handleResult(response = response, handleSuccess = fun() {
                    _applyChatResponse.value = Event(response.body()!!)
                    _snackbar.value = "채팅 신청하기가 완료되었습니다."
                }) {
                    handleTokenExpiration { postApplyChat(id, inUser) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun postSaveFcmToken(token: String) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = userRepository.postSaveFCMToken(SaveFCMTokenRequest(token))

                handleResult(response = response, handleSuccess = fun() {
                    _saveFcmTokenResponse.value = Event(true)
                }) {
                    handleTokenExpiration { postSaveFcmToken(token) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun updateProfile(department: String?, nickname: String, userSelfIntroduction: String?) {
        viewModelScope.launch {
            nickNameCheck.value = false

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
                    _snackbar.value = "프로필 수정이 완료되었습니다."
                }) {
                    handleTokenExpiration {
                        updateProfile(
                            department, nickname, userSelfIntroduction
                        )
                    }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val refreshToken =
                    GNUApplication.sharedPreferences.getString(Constant.X_REFRESH_TOKEN, null)
                if (refreshToken == null) {
                    _expirationToken.value = Event(true)
                    return@launch
                }
                val response = userRepository.postLogout(RefreshTokenRequest(refreshToken))

                handleResult(response = response, handleSuccess = fun() {
                    _logoutResponse.value = Event(response.body()!!)
                    GNUApplication.sharedPreferences.edit().clear().apply()
                    myInfo.value?.let {
                        launch { userRepository.deleteUser(it) }
                    }
                }) {
                    handleTokenExpiration { logoutUser() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
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
                    GNUApplication.sharedPreferences.edit().clear().apply()
                    myInfo.value?.let { launch { userRepository.deleteUser(it) } }
                }) {
                    handleTokenExpiration { withdrawal() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    private fun pagingSourceListener() {
        _expirationToken.value = Event(true)
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
                                _snackbar.value = error.message
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    _spinner.value = false
                    _snackbar.value = "네트워크 에러가 발생했습니다."
                }
            }
        }
    }

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

    fun deleteAlarm(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.deleteNotification(id)

                handleResult(response = response, handleSuccess = fun() {
                    _deleteAlarmResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { deleteAlarm(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun getAlarmList() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.getAlarmList()

                handleResult(response = response, handleSuccess = fun() {
                    _alarmListResponse.value = response.body()!!
                }) {
                    handleTokenExpiration { getAlarmList() }
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


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                return HomeMainViewModel(
                    GNUApplication.userRepository,
                    GNUApplication.postRepository,
                    GNUApplication.applicationRepository,
                    GNUApplication.chatRepository,
                    GNUApplication.alarmRepository
                ) as T
            }
        }
    }
}