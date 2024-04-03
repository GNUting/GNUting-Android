package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.PostDetailResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val postRepository: PostRepository) :
    BaseViewModel() {

    private val _postResponse = MutableLiveData<PostResponse>()

    val postResponse: LiveData<PostResponse> get() = _postResponse


    private val _postDetailResponse = MutableLiveData<PostDetailResponse>()

    val postDetailResponse: LiveData<PostDetailResponse> get() = _postDetailResponse

    private val _deletePostResponse = MutableLiveData<Event<DefaultResponse>>()

    val deletePostResponse: LiveData<Event<DefaultResponse>> get() = _deletePostResponse


    private val _applyChatResponse = MutableLiveData<Event<DefaultResponse>>()

    val applyChatResponse: LiveData<Event<DefaultResponse>>
        get() = _applyChatResponse

    private val _saveResponse = MutableLiveData<Event<DefaultResponse>>()

    val saveResponse: LiveData<Event<DefaultResponse>>
        get() = _saveResponse

    private val _patchPostDetailResponse = MutableLiveData<Event<DefaultResponse>>()

    val patchPostDetailResponse: LiveData<Event<DefaultResponse>>
        get() = _patchPostDetailResponse


    private fun pagingSourceListener() {
        _expirationToken.value = Event(true)
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                    _toast.value = Event("게시물 작성이 완료되었습니다.")
                }) {
                    handleTokenExpiration { postSave(saveRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                    _toast.value = Event("게시물 수정이 완료되었습니다.")
                }) {
                    handleTokenExpiration { patchSave(id, saveRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                    _toast.value = Event("게시물 삭제가 완료되었습니다.")
                }) {
                    handleTokenExpiration { deletePost(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

    fun postApplyChat(id: Int, inUser: List<InUser>) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.postApply(id, inUser)

                handleResult(response = response, handleSuccess = fun() {
                    _toast.value = Event("채팅 신청하기가 완료되었습니다.")
                    _applyChatResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { postApplyChat(id, inUser) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }
}