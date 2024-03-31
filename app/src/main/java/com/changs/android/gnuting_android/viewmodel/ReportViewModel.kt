package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.data.model.ReportCategory
import dagger.hilt.android.lifecycle.HiltViewModel

class ReportViewModel: ViewModel() {
    var reportCategory: ReportCategory = ReportCategory.COMMERCIAL_SPAM
}