package com.changs.android.gnuting_android.di


import android.content.Context
import androidx.room.Room
import com.changs.android.gnuting_android.BuildConfig
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.source.local.AppDatabase
import com.changs.android.gnuting_android.data.source.local.UserDao
import com.changs.android.gnuting_android.data.source.remote.AlarmInterface
import com.changs.android.gnuting_android.data.source.remote.ApplicationInterface
import com.changs.android.gnuting_android.data.source.remote.ChatInterface
import com.changs.android.gnuting_android.data.source.remote.PostInterface
import com.changs.android.gnuting_android.data.source.remote.UserInterface
import com.changs.android.gnuting_android.util.Constant
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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
    fun provideOkHttpClient(): OkHttpClient {
        val requestInterceptor = Interceptor { chain ->
            with(chain) {
                val builder: Request.Builder = chain.request().newBuilder()
                val jwtToken: String? =
                    GNUApplication.sharedPreferences.getString(Constant.X_ACCESS_TOKEN, null)
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
        return OkHttpClient.Builder().readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS).addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor(requestInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(Constant.BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideUserApiService(retrofit: Retrofit): UserInterface {
        return retrofit.create(UserInterface::class.java)
    }

    @Singleton
    @Provides
    fun providePostApiService(retrofit: Retrofit): PostInterface {
        return retrofit.create(PostInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideChatApiService(retrofit: Retrofit): ChatInterface {
        return retrofit.create(ChatInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideApplicationApiService(retrofit: Retrofit): ApplicationInterface {
        return retrofit.create(ApplicationInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideAlarmApiService(retrofit: Retrofit): AlarmInterface {
        return retrofit.create(AlarmInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, Constant.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()
}