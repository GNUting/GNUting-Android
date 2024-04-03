package com.changs.android.gnuting_android.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changs.android.gnuting_android.BuildConfig
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.source.remote.UserInterface
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class BaseViewModel : ViewModel() {
    protected val _toast = MutableLiveData<Event<String?>>()

    val toast: LiveData<Event<String?>>
        get() = _toast

    protected val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner



    protected val _expirationToken = MutableLiveData<Event<Boolean>>()

    val expirationToken: LiveData<Event<Boolean>>
        get() = _expirationToken

    protected suspend fun <T : Any> handleResult(
        response: Response<T>,
        handleSuccess: () -> Unit,
        handleError: (() -> Unit)? = null,
        handleTokenExpiration: suspend () -> Unit
    ) {
        if (response.isSuccessful && response.body() != null) {
            handleSuccess()
            _spinner.value = false
        } else {
            response.errorBody()?.let { errorBody ->
                val error = getErrorResponse(errorBody)
                error?.let {
                    _spinner.value = false
                    when {
                        it.code == "BOARD5003" -> {
                            // Handle specific error
                        }

                        it.code == "TOKEN4001-1" -> {
                            handleTokenExpiration()
                        }

                        it.code != null && it.code.contains("TOKEN4001") -> {
                            _expirationToken.value = Event(true)
                        }

                        else -> {
                            handleError?.let { handle ->
                                handle()
                            }
                            _toast.value = Event("네트워크 에러가 발생했습니다.")
                        }
                    }
                }
            }
        }
    }


    protected suspend fun handleTokenExpiration(reCallApi: () -> Unit) {
        GNUApplication.sharedPreferences.edit().putString(Constant.X_ACCESS_TOKEN, null).apply()
        val refreshToken =
            GNUApplication.sharedPreferences.getString(Constant.X_REFRESH_TOKEN, null)
        if (refreshToken != null) {


            val response = reIssueTokenService().postReIssueAccessToken(
                RefreshTokenRequest(refreshToken)
            )

            if (response.isSuccessful && response.body() != null) {
                val accessToken = response.body()!!.result.accessToken
                GNUApplication.sharedPreferences.edit()
                    .putString(Constant.X_ACCESS_TOKEN, accessToken).apply()

                reCallApi()
            } else {
                _expirationToken.value = Event(true)
            }
        } else {
            _expirationToken.value = Event(true)
        }
    }

    private fun reIssueTokenService(): UserInterface {
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

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(requestInterceptor).build()

        return Retrofit.Builder().baseUrl(Constant.BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(UserInterface::class.java)
    }
}