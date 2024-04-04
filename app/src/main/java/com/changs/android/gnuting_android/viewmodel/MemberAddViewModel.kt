package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                }) {
                    handleTokenExpiration { getSearchUser(nickname) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }
}