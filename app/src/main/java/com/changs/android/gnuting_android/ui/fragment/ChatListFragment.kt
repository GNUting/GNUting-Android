package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentChatListBinding
import com.changs.android.gnuting_android.ui.adapter.ChatListAdapter
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListFragment :
    BaseFragment<FragmentChatListBinding>(FragmentChatListBinding::bind, R.layout.fragment_chat_list) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private var adapter: ChatListAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChatRoomList()
        setRecyclerView()
        setObserver()
    }

    private fun itemClickListener(id: Int, title: String, info: String) {
        val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(id = id, title = title, info = info)
        findNavController().navigate(action)
    }

    private fun setRecyclerView() {
        adapter = ChatListAdapter(::itemClickListener)
        binding.chatListRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        viewModel.chatRoomListResponse.observe(viewLifecycleOwner) {
            adapter?.submitList(it.result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}