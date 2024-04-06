package com.changs.android.gnuting_android.util

import com.changs.android.gnuting_android.data.source.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor @Inject constructor(private val tokenManger: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        with(chain) {
            val builder: Request.Builder = chain.request().newBuilder()

            val jwtToken = runBlocking {
                tokenManger.getAccessToken().firstOrNull()
            }

            if (jwtToken != null) {
                builder.removeHeader(Constant.TOKEN_HEADER).apply {
                    addHeader(
                        Constant.TOKEN_HEADER, "Bearer $jwtToken"
                    )
                }
            }

            return proceed(builder.build())
        }
    }
}