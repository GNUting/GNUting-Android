package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.activityViewModels
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
    private val args: ChatFragmentArgs by navArgs()
    private val adapter by lazy { ChatAdapter(viewModel.myInfo.value?.nickname ?: "") }
    val url = "ws://10.0.2.2:8080/chat" // 소켓에 연결하는 엔드포인트가 /socket일때 다음과 같음

    val connectHeader: HashMap<String, String> = hashMapOf(
        "Authorization" to "Bearer ${
            GNUApplication.sharedPreferences.getString(
                Constant.X_ACCESS_TOKEN, null
            )
        }"
    )
    val requestInterceptor = Interceptor { chain ->
        with(chain) {
            val builder: Request.Builder = chain.request().newBuilder()
            val jwtToken: String? = GNUApplication.sharedPreferences.getString(
                Constant.X_ACCESS_TOKEN, null
            )
            if (jwtToken != null) {
                builder.addHeader("Authorization", "Bearer $jwtToken")
            }

            proceed(builder.build())
        }
    }
    val logger = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    val client = OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(logger)
        .addNetworkInterceptor(requestInterceptor).build()


    private val stompClient by lazy {
        Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, url, connectHeader, client
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChats(args.id)
        setRecyclerView()
        setObserver()
        setListener()

        binding.chatTxtTitle.text = args.title
        binding.chatTxtInfo.text = args.info


        try {
            // 메시지를 받기위한 구독을 설정한다.
            stompClient.topic("/sub/chatRoom/3").subscribe(
                { data ->
                    Log.d("data", data.payload)

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        val messageItem: MessageItem = GsonBuilder().create()
                            .fromJson<MessageItem>(data.payload, MessageItem::class.java)

                        val currentList = adapter.currentList.toMutableList()
                        currentList.add(messageItem)
                        adapter.submitList(currentList) {
                            binding.chatRecyclerview.scrollToPosition(adapter.currentList.size - 1)
                        }
                    }
                },
                { error -> Log.e("error", error.message.toString()) }
            )

            // 헤더를 넣고싶으면 다음과같이 connect할때 설정한 헤더들을 넣어주면 된다.
            val headerList = arrayListOf<StompHeader>()
            headerList.add(
                StompHeader(
                    "Authorization", "Bearer ${
                        GNUApplication.sharedPreferences.getString(
                            Constant.X_ACCESS_TOKEN, null
                        )
                    }"
                )
            )
            stompClient.connect(headerList)

            // 그 다음 코드는 stompClient의 lifeCycle의 변경에 따라 로그를 찍는 코드이다.
            stompClient.lifecycle().subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i("OPEND", "!!")
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        Log.i("CLOSED", "!!")

                    }

                    LifecycleEvent.Type.ERROR -> {
                        Log.i("ERROR", "!!")
                        Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
                    }

                    else -> {
                        Log.i("ELSE", lifecycleEvent.message)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.message.toString())
        }


    }

    private fun setListener() {
        binding.postListImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.chatEdit.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.chatImgSend.performClick()
            }
            false
        })

        binding.chatImgSend.setOnClickListener {
            // 채팅 보낼 떄
            val data = JSONObject()
            // {"messageType": "CHAT", "message": "테스트"}
            data.put("messageType", "CHAT")
            data.put("message", binding.chatEdit.text.toString())

            Log.d("test", data.toString())

            val stompMessage = StompMessage(
                StompCommand.SEND,
                listOf<StompHeader>(
                    StompHeader(StompHeader.DESTINATION, "/pub/chatRoom/3"), StompHeader(
                        "Authorization", "Bearer ${
                            GNUApplication.sharedPreferences.getString(
                                Constant.X_ACCESS_TOKEN, null
                            )
                        }"
                    )
                ),
                data.toString()
            )
            stompClient.send(stompMessage).subscribe()
            binding.chatEdit.text?.clear()
            // stompClient.send("/pub/chatRoom/3", data.toString()).subscribe()

        }
    }

    private fun setRecyclerView() {
        binding.chatRecyclerview.adapter = adapter
        binding.chatRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        viewModel.chatsResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }
    }
}