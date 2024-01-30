package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PolicyViewModel: ViewModel() {
    val isAllChecked = MutableLiveData(false)
}