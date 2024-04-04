package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.ReportCategory
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.UserReportRequest
import com.changs.android.gnuting_android.data.repository.PostRepository
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val postRepository: PostRepository, private val userRepository: UserRepository): BaseViewModel() {
    var reportCategory: ReportCategory = ReportCategory.COMMERCIAL_SPAM

    private val _userReportResponse = MutableLiveData<Event<DefaultResponse>>()

    val userReportResponse: LiveData<Event<DefaultResponse>>
        get() = _userReportResponse

    private val _reportResponse = MutableLiveData<Event<DefaultResponse>>()

    val reportResponse: LiveData<Event<DefaultResponse>>
        get() = _reportResponse


    fun report(reportRequest: ReportRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = postRepository.postReport(reportRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _reportResponse.value = Event(response.body()!!)
                    _toast.value = Event(response.body()!!.result)
                }) {
                    handleTokenExpiration { report(reportRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }


    fun userReport(reportRequest: UserReportRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = userRepository.postUserReport(reportRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _userReportResponse.value = Event(response.body()!!)
                    _toast.value = Event(response.body()!!.result)
                }) {
                    handleTokenExpiration { userReport(reportRequest) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }
}