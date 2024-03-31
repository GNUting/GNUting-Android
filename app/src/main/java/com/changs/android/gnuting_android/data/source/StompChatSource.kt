package com.changs.android.gnuting_android.data.source

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Constant.CHAT_URL
import com.google.gson.GsonBuilder
import com.changs.android.gnuting_android.BuildConfig
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import timber.log.Timber
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit


class StompChatSource(private val chatRoomId: Int) {
    private var subscribe: Disposable? = null
    private var lifecycleSubscribe: Disposable? = null
    private val stompClient: StompClient by lazy {
        val connectHeader: HashMap<String, String> = hashMapOf(
            "Authorization" to "Bearer ${
                GNUApplication.sharedPreferences.getString(
                    Constant.X_ACCESS_TOKEN, null
                )
            }"
        )

        Stomp.over(
            Stomp.ConnectionProvider.OKHTTP, CHAT_URL, connectHeader, createOkhttpClient()
        )
    }


    private fun createOkhttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(logger).build()
    }

    val sharedFlow = MutableSharedFlow<String>()

fun topic() {
        try {
            subscribe = stompClient.topic("/sub/chatRoom/$chatRoomId").subscribe({ data ->
                Timber.i(data.payload)
                CoroutineScope(Dispatchers.IO).launch {
                    sharedFlow.emit(data.payload)
                }

            }, { error -> Timber.e(error.message.toString()) })
        } catch (e: Exception) {
            Timber.e(e.message.toString())
        }
    }

    fun disConnect() {
        try {
            subscribe?.dispose()
            lifecycleSubscribe?.dispose()
            stompClient.disconnect()
        } catch (e: Exception) {
            Timber.e(e.message.toString())
        }
    }

    fun connect() {
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
    }

    fun setLifecycleSubscribe() {
        try {
            lifecycleSubscribe = stompClient.lifecycle().subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Timber.i(lifecycleEvent.message)
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        Timber.i(lifecycleEvent.message)
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Timber.e(lifecycleEvent.exception.toString())
                    }

                    else -> {
                        Timber.i(lifecycleEvent.message)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e.message.toString())
        }
    }

    fun sendMessage(message: String) {
        val data = JSONObject()
        data.put("messageType", "CHAT")
        data.put("message", message)

        Timber.d(data.toString())

        val stompMessage = StompMessage(
            StompCommand.SEND, listOf(
                StompHeader(StompHeader.DESTINATION, "/pub/chatRoom/$chatRoomId"), StompHeader(
                    "Authorization", "Bearer ${
                        GNUApplication.sharedPreferences.getString(
                            Constant.X_ACCESS_TOKEN, null
                        )
                    }"
                )
            ), data.toString()
        )
        stompClient.send(stompMessage).subscribe()
    }
}