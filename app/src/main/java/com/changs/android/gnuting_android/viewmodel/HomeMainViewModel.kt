package com.changs.android.gnuting_android.viewmodel

import android.graphics.Bitmap
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
import com.changs.android.gnuting_android.data.model.ApplicationResponse
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
import com.changs.android.gnuting_android.data.model.ReIssueAccessTokenRequest
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.SaveFCMTokenRequest
import com.changs.android.gnuting_android.data.model.SearchDepartmentResponse
import com.changs.android.gnuting_android.data.repository.ApplicationRepository
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val applicationRepository: ApplicationRepository
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

    private val _reportResponse = MutableLiveData<Event<DefaultResponse>>()

    val reportResponse: LiveData<Event<DefaultResponse>>
        get() = _reportResponse

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
            _snackbar.value = throwable.message
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


    private val _applicationApplyStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationApplyStateResponse: LiveData<ApplicationResponse> get() = _applicationApplyStateResponse

    private val _applicationReceiveStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationReceiveStateResponse: LiveData<ApplicationResponse> get() = _applicationReceiveStateResponse

    private val _searchDepartmentResponse = MutableLiveData<SearchDepartmentResponse>()

    val searchDepartmentResponse: LiveData<SearchDepartmentResponse> get() = _searchDepartmentResponse

    val choiceDepartment = MutableLiveData<String>()

    private val _nickNameCheck = MutableLiveData<Boolean>()

    val nickNameCheck: LiveData<Boolean> get() = _nickNameCheck

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
    fun getCheckNickName() {
        viewModelScope.launch {
            nickname?.let {
                try {
                    _spinner.value = true
                    val result = userRepository.getCheckNickName(it)
                    if (result.isSuccessful && result.body() != null) {
                        _nickNameCheck.value = result.body()!!.result
                        _snackbar.value = result.body()!!.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
                        }
                    }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "네트워크 에러가 발생했습니다."
            }
        }
    }

    fun getMyPostList(page: Int = 1) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val result = postRepository.getMyPostList(page)
                if (result.isSuccessful && result.body() != null) {
                    _myPostResponse.value = result.body()
                    _spinner.value = false

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            _spinner.value = false
                            if (error.code == "BOARD5003") {

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else {
                                _spinner.value = false
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                                        ReIssueAccessTokenRequest(refreshToken)
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

                            } else _snackbar.value = error.message
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
                    _snackbar.value = result.body()!!.result
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
                                        ReIssueAccessTokenRequest(refreshToken)
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
                                        ReIssueAccessTokenRequest(refreshToken)
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
                myInfo.value?.let { userRepository.deleteUser(it) }
                _spinner.value = false
            } catch (e: Exception) {
                _spinner.value = false
                _snackbar.value = "에러가 발생했습니다."
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


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                return HomeMainViewModel(
                    GNUApplication.userRepository,
                    GNUApplication.postRepository,
                    GNUApplication.applicationRepository
                ) as T
            }
        }
    }
}