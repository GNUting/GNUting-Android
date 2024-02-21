package com.changs.android.gnuting_android.util

import com.changs.android.gnuting_android.data.model.DetailPostItem
import com.changs.android.gnuting_android.data.model.PostListItem

interface PostItemNavigator {
    fun navigateToDetail(itemPosition: Int)
}