package com.changs.android.gnuting_android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.repository.ChatRepository
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatViewModel(private val chatRepository: ChatRepository): ViewModel() {

    val url = "ws://localhost:8080/chat" // 소켓에 연결하는 엔드포인트가 /socket일때 다음과 같음
    val connectHeader: HashMap<String, String> = hashMapOf("Authorization" to "")
    val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url, connectHeader)

    fun runStomp() {
        // 메시지를 받기위한 구독을 설정한다.
        stompClient.topic("/sub/chatRoom/3").subscribe { topicMessage ->
            Log.i("message Recieve", topicMessage.payload)
        }

        // 헤더를 넣고싶으면 다음과같이 connect할때 설정한 헤더들을 넣어주면 된다.

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("Authorization",""))
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
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }

        // 채팅 보낼 떄
        val data = JSONObject()
        // {"messageType": "CHAT", "message": "테스트"}
        data.put("messageType", "CHAT")
        data.put("message", "hihi")


        stompClient.send("/pub/chatRoom/3", data.toString()).subscribe()
    }



    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                return ChatViewModel(
                    GNUApplication.chatRepository
                ) as T
            }
        }
    }
}