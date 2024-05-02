package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.util.Constant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CertificationViewModel : ViewModel() {
    var mailCertificationNumberCheck = false

    private val _timerCount = MutableLiveData<Long>(Constant.MIllIS_IN_FUTURE)
    private lateinit var a: Job

    val timerCount: LiveData<Long>
        get() = _timerCount

    fun timerStart() {
        if (::a.isInitialized) a.cancel()

        _timerCount.value = Constant.MIllIS_IN_FUTURE
        a = viewModelScope.launch {
            while (_timerCount.value!! > 0) {
                _timerCount.value = _timerCount.value!!.minus(1000L)
                delay(1000L)
            }
        }
    }

    fun timerStop() {
        if (::a.isInitialized) a.cancel()
    }
}