package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.AlarmListResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.repository.AlarmRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository) :
    BaseViewModel() {
    private val _deleteAlarmResponse = MutableLiveData<Event<DefaultResponse>>()

    val deleteAlarmResponse: LiveData<Event<DefaultResponse>> get() = _deleteAlarmResponse

    private val _alarmListResponse = MutableLiveData<AlarmListResponse>()

    val alarmListResponse: LiveData<AlarmListResponse> get() = _alarmListResponse

    fun deleteAlarm(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.deleteNotification(id)

                handleResult(response = response, handleSuccess = fun() {
                    _deleteAlarmResponse.value = Event(response.body()!!)
                }) {
                    handleTokenExpiration { deleteAlarm(id) }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

    fun getAlarmList() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.getAlarmList()

                handleResult(response = response, handleSuccess = fun() {
                    _alarmListResponse.value = response.body()!!
                }) {
                    handleTokenExpiration { getAlarmList() }
                }
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }
}