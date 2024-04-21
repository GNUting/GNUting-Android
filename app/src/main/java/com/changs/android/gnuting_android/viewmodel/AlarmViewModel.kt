package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.AlarmListResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.NewAlarmResponse
import com.changs.android.gnuting_android.data.model.NotificationSettingRequest
import com.changs.android.gnuting_android.data.model.NotificationSettingResponse
import com.changs.android.gnuting_android.data.repository.AlarmRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository) :
    BaseViewModel() {
    private val _deleteAlarmResponse = MutableLiveData<Event<DefaultResponse>>()

    val deleteAlarmResponse: LiveData<Event<DefaultResponse>> get() = _deleteAlarmResponse

    private val _alarmListResponse = MutableLiveData<AlarmListResponse>()

    val alarmListResponse: LiveData<AlarmListResponse> get() = _alarmListResponse

    private val _newAlarmResponse = MutableLiveData<NewAlarmResponse>()

    val newAlarmResponse: LiveData<NewAlarmResponse> get() = _newAlarmResponse

    private val _currentChatRoomAlarmStatusResponse = MutableLiveData<NotificationSettingResponse>()

    val currentChatRoomAlarmStatusResponse: LiveData<NotificationSettingResponse> get() = _currentChatRoomAlarmStatusResponse

    private val _alarmStatusResponse = MutableLiveData<NotificationSettingResponse>()

    val alarmStatusResponse: LiveData<NotificationSettingResponse> get() = _alarmStatusResponse

    private val _chatNotificationSettingResponse = MutableLiveData<Event<DefaultResponse>>()

    val chatNotificationSettingResponse: LiveData<Event<DefaultResponse>> get() = _chatNotificationSettingResponse

    private val _notificationSettingResponse = MutableLiveData<Event<DefaultResponse>>()

    val notificationSettingResponse: LiveData<Event<DefaultResponse>> get() = _notificationSettingResponse

    fun deleteAlarm(id: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.deleteNotification(id)

                handleResult(response = response, handleSuccess = fun() {
                    _deleteAlarmResponse.value = Event(response.body()!!)
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
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
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun getNewAlarm() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.getNewAlarm()

                handleResult(response = response, handleSuccess = fun() {
                    _newAlarmResponse.value = response.body()!!
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun getCurrentChatRoomNotificationStatus(chatRoomId: Int) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.getCurrentChatRoomNotificationStatus(chatRoomId)

                handleResult(response = response, handleSuccess = fun() {
                    _currentChatRoomAlarmStatusResponse.value = response.body()!!
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun getNotificationStatus() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.getOverallNotificationStatus()

                handleResult(response = response, handleSuccess = fun() {
                    _alarmStatusResponse.value = response.body()!!
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun putCurrentChatRoomNotificationSetting(chatRoomId: Int, notificationSettingRequest: NotificationSettingRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.putCurrentChatRoomNotificationStatus(chatRoomId, notificationSettingRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _chatNotificationSettingResponse.value = Event(response.body()!!)
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun putNotificationSetting(notificationSettingRequest: NotificationSettingRequest) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = alarmRepository.putOverallNotificationStatus(notificationSettingRequest)

                handleResult(response = response, handleSuccess = fun() {
                    _notificationSettingResponse.value = Event(response.body()!!)
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }
}