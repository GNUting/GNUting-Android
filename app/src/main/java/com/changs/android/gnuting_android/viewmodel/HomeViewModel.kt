package com.changs.android.gnuting_android.viewmodel

import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.data.model.HomePostItem

class HomeViewModel: ViewModel() {
    val images = listOf(
        "https://image.lawtimes.co.kr/images/192523.jpg",
        "https://thumb.mtstarnews.com/06/2023/06/2023062215005684112_1.jpg",
        "https://i.ytimg.com/vi/tbg3QAu-GnI/maxresdefault.jpg",
    )

    val posts = listOf(HomePostItem(0, "간호학과", "3:3 미팅하실분 구합니다~"), HomePostItem(1, "컴퓨터공학과", "5:5 미팅하실분 구합니다~"), HomePostItem(2, "의류학과", "2:2 미팅하실분 구합니다~"), HomePostItem(3, "간호학과", "3:3 미팅하실분 구합니다~"))

    var currentItem = 0
}