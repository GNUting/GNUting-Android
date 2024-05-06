package com.changs.android.gnuting_android.ui.fragment.chat

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.AlarmStatus
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.data.model.NotificationSettingRequest
import com.changs.android.gnuting_android.databinding.FragmentChatBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ChatAdapter
import com.changs.android.gnuting_android.ui.adapter.ChatRoomCurrentMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import com.changs.android.gnuting_android.viewmodel.ChatViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ChatFragment :
    BaseFragment<FragmentChatBinding>(FragmentChatBinding::bind, R.layout.fragment_chat) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val alarmViewModel: AlarmViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private var adapter: ChatAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewModel.getChats(args.id)
        chatViewModel.getChatRoomUsers(args.id)
        alarmViewModel.getCurrentChatRoomNotificationStatus(args.id)
        setRecyclerView()
        setObserver()
        setListener()

        binding.chatTxtTitle.text = args.title
        binding.chatTxtInfo.text = args.info
    }

    private fun setListener() {
        binding.chatImgSetting.setOnClickListener {
            it.hideSoftKeyboard()
            if (!binding.chatDrawerMain.isOpen) binding.chatDrawerMain.openDrawer(GravityCompat.END)
        }

        binding.chatEdit.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(300))

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
    }

    private fun setRecyclerView() {
        adapter = ChatAdapter(
            viewModel.myInfo.value?.nickname ?: "", ::navigateListener
        )
        binding.chatRecyclerview.adapter = adapter
        binding.chatRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        chatViewModel.chatRoomUsersResponse.observe(viewLifecycleOwner) {
            with(binding.chatLayoutDrawer) {
                drawerChatImgChatout.setOnClickListener {
                    showTwoButtonDialog(
                        context = requireContext(),
                        titleText = "채팅방을 나가시겠습니까?",
                        rightButtonText = "나가기"
                    ) {
                        chatViewModel.chatRoomLeave(args.id)
                    }
                }

                val adapter =
                    ChatRoomCurrentMemberAdapter(viewModel.myInfo.value?.id, ::navigateListener)
                drawerChatRecycler.adapter = adapter

                val chatRoomUsers = it.result.toMutableList()
                val index = chatRoomUsers.indexOfFirst {
                    it.userId == (viewModel.myInfo.value?.id ?: -1)
                }

                if (index != -1) {
                    val user = chatRoomUsers.removeAt(index)
                    chatRoomUsers.add(0, user)
                }

                adapter.submitList(chatRoomUsers.toList())
            }
        }

        alarmViewModel.currentChatRoomAlarmStatusResponse.observe(viewLifecycleOwner) {
            when (it.result.notificationSetting) {
                AlarmStatus.ENABLE.name -> {
                    binding.chatLayoutDrawer.drawerChatCheckBell.isChecked = true
                }

                AlarmStatus.DISABLE.name -> {
                    binding.chatLayoutDrawer.drawerChatCheckBell.isChecked = false
                }
            }

            binding.chatLayoutDrawer.drawerChatCheckBell.setOnCheckedChangeListener { buttonView, isChecked ->
                val requestSettingStatus =
                    if (isChecked) AlarmStatus.ENABLE.name else AlarmStatus.DISABLE.name

                alarmViewModel.putCurrentChatRoomNotificationSetting(
                    args.id, NotificationSettingRequest(requestSettingStatus)
                )

            }
        }

        chatViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        chatViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        val mutex = Mutex()

        chatViewModel.message.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                mutex.withLock {
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