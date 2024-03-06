package com.changs.android.gnuting_android.data.repository

import android.graphics.Bitmap
import com.changs.android.gnuting_android.data.model.CheckNickNameResponse
import com.changs.android.gnuting_android.data.model.LoginRequest
import com.changs.android.gnuting_android.data.model.MailCertificationRequest
import com.changs.android.gnuting_android.data.model.MailCertificationResponse
import com.changs.android.gnuting_android.data.model.SignUpResponse
import com.changs.android.gnuting_android.data.source.remote.UserInterface
import com.changs.android.gnuting_android.util.FormDataUtil
import retrofit2.Response
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val service = retrofit.create(UserInterface::class.java)
    suspend fun postMailCertification(mailCertificationRequest: MailCertificationRequest): Response<MailCertificationResponse> =
        service.postMailCertification(mailCertificationRequest)

    suspend fun getCheckNickName(nickname: String): Response<CheckNickNameResponse> =
        service.getCheckNickName(nickname)

    suspend fun postSignUp(
        birthDate: String,
        department: String,
        email: String,
        gender: String,
        name: String,
        nickname: String,
        password: String,
        phoneNumber: String,
        studentId: String,
        profileImage: Bitmap?,
        userSelfIntroduction: String
    ): Response<SignUpResponse> {
        return service.postSignUp(
            birthDate = FormDataUtil.getTextBody("birthDate", birthDate),
            department = FormDataUtil.getTextBody("department", department),
            email = FormDataUtil.getTextBody("email", email),
            gender = FormDataUtil.getTextBody("gender", gender),
            name = FormDataUtil.getTextBody("name", name),
            nickname = FormDataUtil.getTextBody("nickname", nickname),
            password = FormDataUtil.getTextBody("password", password),
            phoneNumber = FormDataUtil.getTextBody("phoneNumber", phoneNumber),
            studentId = FormDataUtil.getTextBody("studentId", studentId),
            profileImage = profileImage?.let { FormDataUtil.getImageBody(profileImage, "profileImage", "profileImage") },
            userSelfIntroduction = FormDataUtil.getTextBody("userSelfIntroduction", userSelfIntroduction)
        )
    }

    suspend fun getSearchDepartment(name: String) = service.getSearchDepartment(name)

    suspend fun postLogin(loginRequest: LoginRequest) = service.postLogin(loginRequest)
}