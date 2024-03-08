package com.changs.android.gnuting_android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.GNUApplication.Companion.postRepository
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.MyInfoResponse
import com.changs.android.gnuting_android.data.model.MyInfoResult
import com.changs.android.gnuting_android.data.model.PostDetailResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

@ExperimentalCoroutinesApi
class HomeMainViewModel(userRepository: UserRepository, postRepository: PostRepository) : ViewModel() {
    // TODO: Flow 로 코드 개선 예정
    private val myInfoFlow = MutableStateFlow<MyInfoResponse?>(null)

    val myInfo: LiveData<MyInfoResult> = myInfoFlow.flatMapLatest {
        userRepository.myInfoFlow
    }.asLiveData()

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
        myInfoFlow
            .onEach {
                _spinner.value = true
                userRepository.fetchRecentMyInfo()
            }.onEach {
                _spinner.value = false
            }.catch { throwable ->
                _snackbar.value = throwable.message
                _spinner.value = false
            }.launchIn(viewModelScope)
    }

    val members = listOf(
        Member(
            null,
            "전재욱",
            "짱짱맨",
            "19학번",
            "25살",
            "ENTP",
            "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.",
            "asd123",
            "컴퓨터과학과"
        )
    )

    val images = listOf(
        "https://image.lawtimes.co.kr/images/192523.jpg",
        "https://thumb.mtstarnews.com/06/2023/06/2023062215005684112_1.jpg",
        "https://i.ytimg.com/vi/tbg3QAu-GnI/maxresdefault.jpg",
    )

    var currentItem = 0

    private val _postResponse = MutableLiveData<PostResponse>()

    val postResponse: LiveData<PostResponse> get() = _postResponse

    private val _postDetailResponse = MutableLiveData<PostDetailResponse>()

    val postDetailResponse: LiveData<PostDetailResponse> get() = _postDetailResponse


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
                            _snackbar.value = error.message
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
                            _spinner.value = false
                            _snackbar.value = error.message
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
                    GNUApplication.userRepository, GNUApplication.postRepository
                ) as T
            }
        }
    }
}