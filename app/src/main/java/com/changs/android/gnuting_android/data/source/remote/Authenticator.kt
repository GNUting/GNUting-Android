package com.changs.android.gnuting_android.data.source.remote

import android.content.Context
import android.content.Intent
import com.changs.android.gnuting_android.BuildConfig
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.Constant
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Authenticator(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken =
                GNUApplication.sharedPreferences.getString(Constant.X_REFRESH_TOKEN, null)

            if (refreshToken == null) {
                response.close()
                null
            } else {
                newRequestWithToken(refreshToken, response.request)
            }
        }
    }

    private suspend fun newRequestWithToken(refreshToken: String, request: Request): Request? {
        val accessToken = callApiReissueToken(refreshToken)
        return if (accessToken == null) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            startLoginActivity()
            null
        } else {
            GNUApplication.sharedPreferences.edit().putString(Constant.X_ACCESS_TOKEN, accessToken)
                .apply()

            request.newBuilder().removeHeader("Authorization").apply {
                addHeader("Authorization", "Bearer $accessToken")
            }.build()
        }
    }


    private suspend fun callApiReissueToken(refreshToken: String): String? {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        val okHttpClient = OkHttpClient.Builder().readTimeout(Constant.READ_TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(Constant.CONNECT_TIME_OUT, TimeUnit.MILLISECONDS).addInterceptor(httpLoggingInterceptor)
            .build()

        val reissueTokenService = Retrofit.Builder().baseUrl(Constant.BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(UserService::class.java)

        return reissueTokenService.postReIssueAccessToken(RefreshTokenRequest(refreshToken))
            .body()?.result?.accessToken
    }

    private fun startLoginActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}