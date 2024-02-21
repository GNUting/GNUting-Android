package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.data.model.Member

class PostViewModel: ViewModel() {
    val members = listOf(Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"))
}