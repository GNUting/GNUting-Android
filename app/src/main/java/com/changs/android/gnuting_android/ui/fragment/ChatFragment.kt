package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ChatItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentChatBinding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.adapter.ChatAdapter

class ChatFragment :
    BaseFragment<FragmentChatBinding>(FragmentChatBinding::bind, R.layout.fragment_chat) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
    }

    private fun setRecyclerView() {
        val adapter = ChatAdapter()
        binding.chatRecyclerview.adapter = adapter

        val chatList = listOf(
            ChatItem(
                "4:4 과팅하실분 연락 주세요~",
                "저희 주당에서 7시에 볼까요?",
                Member(
                    null,
                    "전재욱",
                    "짱짱맨",
                    "19학번",
                    "25살",
                    "ENTP",
                    "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.",
                    "asd123",
                    "컴퓨터과학과"
                )
            ),
            ChatItem(
                "4:4 과팅하실분 연락 주세요~",
                "저희 주당에서 7시에 볼까요?",
                Member(
                    null,
                    "전재욱",
                    "짱짱맨",
                    "19학번",
                    "25살",
                    "ENTP",
                    "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.",
                    "asd123",
                    "컴퓨터과학과"
                )
            )
        )

        adapter.submitList(chatList)
    }
}