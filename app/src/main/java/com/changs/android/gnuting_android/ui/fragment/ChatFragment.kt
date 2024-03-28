package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.databinding.FragmentChatBinding
import com.changs.android.gnuting_android.ui.adapter.ChatAdapter
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.viewmodel.ChatViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import timber.log.Timber
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalCoroutinesApi::class)
class ChatFragment :
    BaseFragment<FragmentChatBinding>(FragmentChatBinding::bind, R.layout.fragment_chat) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private val adapter by lazy { ChatAdapter(viewModel.myInfo.value?.nickname ?: "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChats(args.id)
        setRecyclerView()
        setObserver()
        setListener()

        binding.chatTxtTitle.text = args.title
        binding.chatTxtInfo.text = args.info

    }

    private fun updateMessage(data: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val messageItem: MessageItem =
                GsonBuilder().create().fromJson(data, MessageItem::class.java)

            if (messageItem.messageType == "CHAT") {
                val currentList = adapter.currentList.toMutableList()
                currentList.add(messageItem)
                adapter.submitList(currentList) {
                    binding.chatRecyclerview.scrollToPosition(adapter.currentList.size - 1)
                }
            }

        }
    }

    private fun setListener() {
        binding.postListImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chatEdit.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.chatImgSend.performClick()
            }
            false
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
        binding.chatRecyclerview.adapter = adapter
        binding.chatRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        viewModel.chatsResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)

            chatViewModel.connectChatRoom(args.id, ::updateMessage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.disConnectChatRoom()
    }
}