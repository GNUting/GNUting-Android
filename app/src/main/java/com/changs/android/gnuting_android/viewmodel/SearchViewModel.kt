package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.PostSearchResponse
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchViewModel(private val postRepository: PostRepository) : ViewModel() {
    private val _searchPostResponse = MutableLiveData<PostSearchResponse>()

    val searchPostResponse: LiveData<PostSearchResponse>
        get() = _searchPostResponse

    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar
    fun onSnackbarShown() {
        _snackbar.value = null
    }

    fun getSearchPost(keyword: String) {
        viewModelScope.launch {
            try {
                val result = postRepository.getSearchPost(keyword)
                if (result.isSuccessful && result.body() != null) {
                    _searchPostResponse.value = result.body()

                } else {
                    result.errorBody()?.let {
                        val errorBody = getErrorResponse(it)
                        errorBody?.let { error ->
                            if (error.code == "BOARD5003") {
                                // TODO: 분기 처리 추가
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
                return SearchViewModel(
                    GNUApplication.postRepository
                ) as T
            }
        }
    }
}