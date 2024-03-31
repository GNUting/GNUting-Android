package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class PolicyViewModel: ViewModel() {
    val isAllChecked = MutableLiveData(false)
}