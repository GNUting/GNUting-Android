package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.data.model.PostListItem

class PostListViewModel: ViewModel() {
    val postItems = listOf(PostListItem(1, "컴퓨터공학과", "5:5 미팅하실분 구합니다~", "16학번"), PostListItem(2, "의류학과", "2:2 미팅하실분 구합니다~", "23학번"), PostListItem(3, "기계공학과", "3:3 미팅하실분 구합니다~", "22학번"))
}