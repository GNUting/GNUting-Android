package com.changs.android.gnuting_android.ui.fragment.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentChatListBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.ChatListAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.ChatViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ChatListFragment :
    BaseFragment<FragmentChatListBinding>(FragmentChatListBinding::bind, R.layout.fragment_chat_list) {
    private val chatViewModel: ChatViewModel by viewModels()
    private var adapter: ChatListAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel.getChatRoomList()
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
        chatViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        chatViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        chatViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        chatViewModel.chatRoomListResponse.observe(viewLifecycleOwner) {
            adapter?.submitList(it.result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}