package com.changs.android.gnuting_android.di


import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.changs.android.gnuting_android.BuildConfig
import com.changs.android.gnuting_android.data.source.local.AppDatabase
import com.changs.android.gnuting_android.data.source.local.TokenManager
import com.changs.android.gnuting_android.data.source.local.UserDao
import com.changs.android.gnuting_android.data.source.remote.AlarmService
import com.changs.android.gnuting_android.data.source.remote.ApplicationService
import com.changs.android.gnuting_android.data.source.remote.ChatService
import com.changs.android.gnuting_android.data.source.remote.PostService
import com.changs.android.gnuting_android.data.source.remote.UserService
import com.changs.android.gnuting_android.util.Authenticator
import com.changs.android.gnuting_android.util.Constant.BASE_URL
import com.changs.android.gnuting_android.util.Constant.CONNECT_TIME_OUT
import com.changs.android.gnuting_android.util.Constant.DATABASE_NAME
import com.changs.android.gnuting_android.util.Constant.READ_TIME_OUT
import com.changs.android.gnuting_android.util.Constant.TOKEN_PREFERENCE_STORE
import com.changs.android.gnuting_android.util.RequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        val dataStore = PreferenceDataStoreFactory.create(produceFile = {
            context.preferencesDataStoreFile(
                TOKEN_PREFERENCE_STORE
            )
        })
        return TokenManager(dataStore)
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Singleton
    @Provides
    fun provideAuthenticator(@ApplicationContext context: Context, tokenManager: TokenManager): Authenticator =
        Authenticator(context, tokenManager)

    @Singleton
    @Provides
    fun provideRequestInterceptor(tokenManager: TokenManager): Interceptor = RequestInterceptor(tokenManager)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        requestInterceptor: Interceptor,
        authenticator: Authenticator
    ): OkHttpClient = OkHttpClient.Builder().readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
        .addInterceptor(httpLoggingInterceptor).addInterceptor(requestInterceptor)
        .authenticator(authenticator).build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideUserApiService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Singleton
    @Provides
    fun providePostApiService(retrofit: Retrofit): PostService {
        return retrofit.create(PostService::class.java)
    }

    @Singleton
    @Provides
    fun provideChatApiService(retrofit: Retrofit): ChatService {
        return retrofit.create(ChatService::class.java)
    }

    @Singleton
    @Provides
    fun provideApplicationApiService(retrofit: Retrofit): ApplicationService {
        return retrofit.create(ApplicationService::class.java)
    }

    @Singleton
    @Provides
    fun provideAlarmApiService(retrofit: Retrofit): AlarmService {
        return retrofit.create(AlarmService::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()
}