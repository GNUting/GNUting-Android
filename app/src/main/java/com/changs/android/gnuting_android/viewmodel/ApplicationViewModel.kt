package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.ApplicationResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.repository.ApplicationRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(private val applicationRepository: ApplicationRepository) :
    BaseViewModel() {
    private val _applicationApplyStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationApplyStateResponse: LiveData<ApplicationResponse> get() = _applicationApplyStateResponse

    private val _applicationReceiveStateResponse = MutableLiveData<ApplicationResponse>()
    val applicationReceiveStateResponse: LiveData<ApplicationResponse> get() = _applicationReceiveStateResponse

    private val _refuseResponse = MutableLiveData<Event<DefaultResponse>>()

    val refuseResponse: LiveData<Event<DefaultResponse>>
        get() = _refuseResponse

    private val _acceptResponse = MutableLiveData<Event<DefaultResponse>>()

    val acceptResponse: LiveData<Event<DefaultResponse>>
        get() = _acceptResponse

    private val _cancelResponse = MutableLiveData<Event<DefaultResponse>>()

    val cancelResponse: LiveData<Event<DefaultResponse>>
        get() = _cancelResponse

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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
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
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

}