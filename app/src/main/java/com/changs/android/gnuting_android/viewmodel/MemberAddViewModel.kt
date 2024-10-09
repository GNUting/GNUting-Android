package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseResponse
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MemberAddViewModel @Inject constructor(private val postRepository: PostRepository) :
    BaseViewModel() {
    val currentMember = MutableLiveData<MutableList<InUser>>()

    val searchUserResponse = MutableLiveData<UserSearchResponse>()

    fun getSearchUser(nickname: String) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.getUserSearch(nickname)

                handleResult(response = response, handleSuccess = fun() {
                    searchUserResponse.value = response.body()
                }, handleError = fun(error: BaseResponse) {
                    if (error.code == "USER4000-6") {
                        _toast.value = Event("일치하는 닉네임이 없습니다.")
                    } else {
                        _toast.value = Event("네트워크 에러가 발생했습니다.")
                    }
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }
}