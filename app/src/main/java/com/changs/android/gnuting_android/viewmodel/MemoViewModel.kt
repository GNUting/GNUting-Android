package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changs.android.gnuting_android.base.BaseViewModel
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.data.repository.MemoRepository
import com.changs.android.gnuting_android.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(private val memoRepository: MemoRepository) :
    BaseViewModel() {

    private val _remainingCount = MutableLiveData<Int>()
    val remainingCount: LiveData<Int> get() = _remainingCount

    fun getMemoPagingList(): Flow<PagingData<MemoResult>> {
        return memoRepository.getMemoListPagingData().cachedIn(viewModelScope)
    }


    fun getMemoRemainingCount() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                val response = memoRepository.getMemoRemainingCount()

                handleResult(response = response, handleSuccess = fun() {
                    _remainingCount.value = response.body()?.result
                })
            } catch (e: Exception) {
                _spinner.value = false
                _toast.value = Event("네트워크 에러가 발생했습니다.")
                Timber.e(e.message ?: "network error")
            }
        }
    }
}