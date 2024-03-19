package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class MemberAddViewModel(private val postRepository: PostRepository) : ViewModel() {
    val currentMember = MutableLiveData<MutableList<InUser>>()

    val searchUserResponse = MutableLiveData<UserSearchResponse>()

    private val _expirationToken = MutableLiveData<Event<Boolean>>()

    val expirationToken: LiveData<Event<Boolean>>
        get() = _expirationToken

    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    fun getSearchUser(nickname: String) {
        viewModelScope.launch {
            try {
                val result = postRepository.getUserSearch(nickname)
                if (result.isSuccessful && result.body() != null) {
                    searchUserResponse.value = result.body()
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
                                    val response = postRepository.postReIssueAccessToken(
                                        RefreshTokenRequest(refreshToken)
                                    )

                                    if (response.isSuccessful && response.body() != null) {
                                        val accessToken = response.body()!!.result.accessToken
                                        GNUApplication.sharedPreferences.edit()
                                            .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()
                                        getSearchUser(nickname)
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
                return MemberAddViewModel(
                    GNUApplication.postRepository
                ) as T
            }
        }
    }
}