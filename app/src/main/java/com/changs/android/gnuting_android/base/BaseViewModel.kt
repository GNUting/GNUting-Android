package com.changs.android.gnuting_android.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import retrofit2.Response

open class BaseViewModel : ViewModel() {
    protected val _toast = MutableLiveData<Event<String?>>()

    val toast: LiveData<Event<String?>>
        get() = _toast

    protected val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    protected fun <T : Any> handleResult(
        response: Response<T>, handleSuccess: () -> Unit, handleError: (() -> Unit)? = null
    ) {
        if (response.isSuccessful && response.body() != null) {
            handleSuccess()
            _spinner.value = false
        } else {
            response.errorBody()?.let { errorBody ->
                val error = getErrorResponse(errorBody)
                error?.let {
                    _spinner.value = false
                    when (it.code) {
                        "BOARD5003" -> {
                            // Handle specific error
                        }

                        else -> {
                            handleError?.let { handle ->
                                handle()
                            } ?: {
                                _toast.value = Event("네트워크 에러가 발생했습니다.")
                            }
                        }
                    }
                }
            }
        }
    }
}