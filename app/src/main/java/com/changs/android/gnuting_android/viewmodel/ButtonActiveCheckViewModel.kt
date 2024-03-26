package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ButtonActiveCheckViewModel: ViewModel() {
    val buttonActiveCheck: MutableLiveData<Boolean> = MutableLiveData(false)
}