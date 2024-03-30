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
                val result = applicationRepository.getReceiveApplicationList()
                if (result.isSuccessful && result.body() != null) {
                    _applicationReceiveStateResponse.value = result.body()
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getApplicationReceiveList()
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = applicationRepository.getApplyApplicationList()
                if (result.isSuccessful && result.body() != null) {
                    _applicationApplyStateResponse.value = result.body()
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getApplicationApplyList()
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.getPostList(page)
                if (result.isSuccessful && result.body() != null) {
                    _postResponse.value = result.body()
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getPostList(page)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.getPostDetail(id)
                if (result.isSuccessful && result.body() != null) {
                    _postDetailResponse.value = result.body()
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getPostDetail(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.postSave(saveRequest)
                if (result.isSuccessful && result.body() != null) {
                    _saveResponse.value = Event(result.body()!!)
                    _snackbar.value = "게시물 작성이 완료되었습니다."
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        postSave(saveRequest)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = applicationRepository.patchRefuse(id)
                if (result.isSuccessful && result.body() != null) {
                    _refuseResponse.value = Event(result.body()!!)
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        refuse(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = applicationRepository.postAccept(id)
                if (result.isSuccessful && result.body() != null) {
                    _acceptResponse.value = Event(result.body()!!)
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        accept(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = applicationRepository.deleteCancel(id)
                if (result.isSuccessful && result.body() != null) {
                    _cancelResponse.value = Event(result.body()!!)
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        cancel(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.postReport(reportRequest)
                if (result.isSuccessful && result.body() != null) {
                    _reportResponse.value = Event(result.body()!!)
                    _snackbar.value = result.body()!!.result
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        report(reportRequest)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = userRepository.postUserReport(reportRequest)
                if (result.isSuccessful && result.body() != null) {
                    _userReportResponse.value = Event(result.body()!!)
                    _snackbar.value = result.body()!!.result
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        userReport(reportRequest)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.patchPostDetail(id, saveRequest)
                if (result.isSuccessful && result.body() != null) {
                    _patchPostDetailResponse.value = Event(result.body()!!)
                    _snackbar.value = "게시물 수정이 완료되었습니다."
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        patchSave(id, saveRequest)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.deletePost(id)
                if (result.isSuccessful && result.body() != null) {
                    _deletePostResponse.value = Event(result.body()!!)
                    _snackbar.value = "게시물 삭제가 완료되었습니다."
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        deletePost(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = postRepository.postApply(id, inUser)
                if (result.isSuccessful && result.body() != null) {
                    _applyChatResponse.value = Event(result.body()!!)
                    _spinner.value = false
                    _snackbar.value = "채팅 신청하기가 완료되었습니다."
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        postApplyChat(id, inUser)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = error.message
                            }
                        }
                    }
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
                val result = userRepository.postSaveFCMToken(SaveFCMTokenRequest(token))
                if (result.isSuccessful && result.body() != null) {
                    _saveFcmTokenResponse.value = Event(true)
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        postSaveFcmToken(token)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            }
                        }
                    }
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
                val result = userRepository.patchProfile(
                    department = department,
                    nickname = nickname,
                    profileImage = profileImage,
                    userSelfIntroduction = userSelfIntroduction
                )
                if (result.isSuccessful && result.body() != null) {
                    _profileResponse.value = Event(result.body()!!)
                    _snackbar.value = "프로필 수정이 완료되었습니다."
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        updateProfile(department, nickname, userSelfIntroduction)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
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
                val result = userRepository.postLogout(RefreshTokenRequest(refreshToken))
                if (result.isSuccessful && result.body() != null) {
                    _logoutResponse.value = Event(result.body()!!)
                    GNUApplication.sharedPreferences.edit().clear().apply()
                    myInfo.value?.let { userRepository.deleteUser(it) }
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val response = userRepository.postReIssueAccessToken(
                                    RefreshTokenRequest(refreshToken)
                                )

                                if (response.isSuccessful && response.body() != null) {
                                    val accessToken = response.body()!!.result.accessToken
                                    GNUApplication.sharedPreferences.edit()
                                        .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                    logoutUser()
                                } else {
                                    _expirationToken.value = Event(true)
                                }
                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = userRepository.deleteWithdrawal()
                if (result.isSuccessful && result.body() != null) {
                    _withdrawalResponse.value = Event(result.body()!!)
                    GNUApplication.sharedPreferences.edit().clear().apply()
                    myInfo.value?.let { userRepository.deleteUser(it) }
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        withdrawal()
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            } else {
                                _snackbar.value = "네트워크 에러가 발생했습니다."
                            }
                        }
                    }
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
                val result = chatRepository.getChatRoomList()
                if (result.isSuccessful && result.body() != null) {
                    _chatRoomListResponse.value = result.body()!!
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getChatRoomList()
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            }
                        }
                    }
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
                val result = chatRepository.getChats(chatRoomId)
                if (result.isSuccessful && result.body() != null) {
                    _chatsResponse.value = result.body()!!
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getChats(chatRoomId)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            }
                        }
                    }
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
                val result = alarmRepository.deleteNotification(id)
                if (result.isSuccessful && result.body() != null) {
                    _deleteAlarmResponse.value = Event(result.body()!!)
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        deleteAlarm(id)
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            }
                        }
                    }
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
                val result = alarmRepository.getAlarmList()
                if (result.isSuccessful && result.body() != null) {
                    _alarmListResponse.value = result.body()!!
                    _spinner.value = false
                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else if (error.code == "TOKEN4001-1") {
                                GNUApplication.sharedPreferences.edit()
                                    .putString(Constant.X_ACCESS_TOKEN, null).apply()

                                val refreshToken = GNUApplication.sharedPreferences.getString(
                                    Constant.X_REFRESH_TOKEN, null
                                )

                                if (refreshToken != null) {
                                    val response = userRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getAlarmList()
                                    } else {
                                        _expirationToken.value = Event(true)
                                    }
                                } else {
                                    _expirationToken.value = Event(true)
                                }

                            } else if (error.code != null && error.code.contains("TOKEN4001")) {
                                _expirationToken.value = Event(true)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
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