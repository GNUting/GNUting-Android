package com.changs.android.gnuting_android.ui.fragment.chat

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.databinding.FragmentChatBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ChatAdapter
import com.changs.android.gnuting_android.ui.fragment.post.DetailFragmentDirections
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.ChatViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ChatFragment :
    BaseFragment<FragmentChatBinding>(FragmentChatBinding::bind, R.layout.fragment_chat) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private var adapter: ChatAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel.getChats(args.id)
        setRecyclerView()
        setObserver()
        setListener()

        binding.chatTxtTitle.text = args.title
        binding.chatTxtInfo.text = args.info
    }

    private fun setListener() {
        val inputFilter = InputFilter { _, _, _, dest, dstart, _ ->
            // 입력된 텍스트에서 줄 수 계산
            val lineCount = dest.toString().substring(0, dstart).split("\n").size

            // 20줄 이상인 경우 입력 제한
            if (lineCount >= 20) ""
            else null
        }

        binding.chatEdit.filters = arrayOf(inputFilter)

        binding.postListImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chatImgSend.setOnClickListener {
            if (!binding.chatEdit.text.isNullOrEmpty()) {
                chatViewModel.sendMessage(binding.chatEdit.text.toString())
                binding.chatEdit.text?.clear()
                binding.chatEdit.clearFocus()
            }
        }

/*        binding.chatImgSetting.setOnClickListener {
            if (binding.chatLlSpinner.isVisible) {
                binding.chatLlSpinner.visibility = View.GONE
            } else {
                binding.chatLlSpinner.visibility = View.VISIBLE
            }
        }

        binding.chatTxtMenuLeave.setOnClickListener {
            chatViewModel.chatRoomLeave(args.id)
        }*/
    }

    private fun setRecyclerView() {
        adapter = ChatAdapter(
            viewModel.myInfo.value?.nickname ?: "", ::navigateListener
        )
        binding.chatRecyclerview.adapter = adapter
        binding.chatRecyclerview.itemAnimator = null
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

        chatViewModel.message.observe(viewLifecycleOwner) {
            val messageItem: MessageItem =
                GsonBuilder().create().fromJson(it, MessageItem::class.java)

            adapter?.let {
                val currentList = it.currentList.toMutableList()
                currentList.add(messageItem)
                it.submitList(currentList) {
                    binding.chatRecyclerview.scrollToPosition(it.currentList.size - 1)
                }
            }
        }
        chatViewModel.chatsResponse.observe(viewLifecycleOwner) { response ->
            adapter?.let {
                it.submitList(response.result) {
                    binding.chatRecyclerview.scrollToPosition(it.currentList.size - 1)
                }
            }

            chatViewModel.connectChatRoom(args.id)
        }

        chatViewModel.chatRoomLeaveResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        GNUApplication.isActiveChatFragment = true
    }

    override fun onPause() {
        super.onPause()
        GNUApplication.isActiveChatFragment = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.disConnectChatRoom()
        adapter = null
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.photoFragment, args)
    }
}