package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.CheckNickNameResponse
import com.changs.android.gnuting_android.data.model.LoginRequest
import com.changs.android.gnuting_android.data.model.LoginResponse
import com.changs.android.gnuting_android.data.model.MailCertificationRequest
import com.changs.android.gnuting_android.data.model.MailCertificationResponse
import com.changs.android.gnuting_android.data.model.MyInfoResponse
import com.changs.android.gnuting_android.data.model.SearchDepartmentResponse
import com.changs.android.gnuting_android.data.model.SignUpResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserInterface {
    @POST("api/v1/mail")
    suspend fun postMailCertification(@Body mailCertificationRequest: MailCertificationRequest): Response<MailCertificationResponse>

    @GET("/api/v1/check-nickname")
    suspend fun getCheckNickName(@Query("nickname") nickname: String): Response<CheckNickNameResponse>

    @Multipart
    @POST("/api/v1/signup")
    suspend fun postSignUp(
        @Part birthDate: MultipartBody.Part,
        @Part department: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part gender: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part nickname: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part phoneNumber: MultipartBody.Part,
        @Part studentId: MultipartBody.Part,
        @Part profileImage: MultipartBody.Part? = null,
        @Part userSelfIntroduction: MultipartBody.Part
    ): Response<SignUpResponse>

    @GET("/api/v1/search-department")
    suspend fun getSearchDepartment(@Query("name") name: String): Response<SearchDepartmentResponse>

    @POST("/api/v1/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("/api/v1/board/user/myinfo")
    suspend fun getMyInfo(): MyInfoResponse
}