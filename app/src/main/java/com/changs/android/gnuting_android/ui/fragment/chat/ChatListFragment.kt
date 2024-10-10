package com.changs.android.gnuting_android.ui.fragment.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ChatRoomUser
import com.changs.android.gnuting_android.databinding.FragmentChatListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ChatListAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ChatListFragment :
    BaseFragment<FragmentChatListBinding>(
        FragmentChatListBinding::bind,
        R.layout.fragment_chat_list
    ) {
    private val chatViewModel: ChatViewModel by viewModels()
    private var adapter: ChatListAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setObserver()
    }

    private fun itemClickListener(
        id: Int,
        title: String,
        info: String,
        chatRoomUsers: List<ChatRoomUser?>
    ) {
        val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(id = id)
        findNavController().navigate(action)
    }

    override fun onResume() {
        chatViewModel.startPollingChatRoomList()
        super.onResume()
    }

    override fun onPause() {
        chatViewModel.stopPollingChatRoomList()
        super.onPause()
    }

    private fun setRecyclerView() {
        adapter = ChatListAdapter(::itemClickListener)
        binding.chatListRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        chatViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        chatViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.chatRoomListResponseFlow.flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.RESUMED
            ).collectLatest { response ->
                if (response != null) {
                    adapter?.submitList(response.result)

                    if (response.result.isNotEmpty()) {
                        binding.chatListLlEmpty.visibility = View.GONE
                    } else {
                        binding.chatListLlEmpty.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }
}