package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changs.android.gnuting_android.base.BaseResponse
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.ApplyChatResponse
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.data.model.PostEventRequestBody
import com.changs.android.gnuting_android.data.model.PostMemoRequestBody
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.repository.EventRepository
import com.changs.android.gnuting_android.data.repository.MemoRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(private val eventRepository: EventRepository) :
    BaseViewModel() {
    private val _postEventResponse = MutableLiveData<Event<ApplyChatResponse>>()

    val postEventResponse: LiveData<Event<ApplyChatResponse>>
        get() = _postEventResponse

    private val _eventCheckResponse = MutableLiveData<String>()
    val eventCheckResponse: LiveData<String> get() = _eventCheckResponse

    fun getEventCheck() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = eventRepository.getEventCheck()

                handleResult(response = response, handleSuccess = fun() {
                   _eventCheckResponse.value = response.body()?.result
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }

    fun postEvent(request: PostEventRequestBody) {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = eventRepository.postEvent(request)

                handleResult(response = response, handleSuccess = fun() {
                    _postEventResponse.value = Event(response.body()!!)
                }, handleError = fun(error: BaseResponse) {
                    _toast.value = Event(error.message)
                })
            } catch (e: Exception) {
                _spinner.value = false
                Timber.e(e.message ?: "network error")
            }
        }
    }
}