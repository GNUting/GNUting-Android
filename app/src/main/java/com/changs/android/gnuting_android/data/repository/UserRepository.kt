package com.changs.android.gnuting_android.data.repository

import android.graphics.Bitmap
import com.changs.android.gnuting_android.data.model.CheckNickNameResponse
import com.changs.android.gnuting_android.data.model.EmailVerifyRequest
import com.changs.android.gnuting_android.data.model.LoginRequest
import com.changs.android.gnuting_android.data.model.LogoutRequest
import com.changs.android.gnuting_android.data.model.MailCertificationRequest
import com.changs.android.gnuting_android.data.model.MailCertificationResponse
import com.changs.android.gnuting_android.data.model.MyInfoResult
import com.changs.android.gnuting_android.data.model.PasswordRequest
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.model.SaveFCMTokenRequest
import com.changs.android.gnuting_android.data.model.SignUpResponse
import com.changs.android.gnuting_android.data.model.UserReportRequest
import com.changs.android.gnuting_android.data.source.local.AppDatabase
import com.changs.android.gnuting_android.data.source.remote.UserService
import com.changs.android.gnuting_android.util.FormDataUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val service: UserService, room: AppDatabase) {
    private val dao = room.userDao()
    suspend fun postMailCertification(mailCertificationRequest: MailCertificationRequest): Response<MailCertificationResponse> =
        service.postMailCertification(mailCertificationRequest)

    suspend fun postFindPasswordMailCertification(mailCertificationRequest: MailCertificationRequest): Response<MailCertificationResponse> =
        service.postFindPasswordMailCertification(mailCertificationRequest)

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
        userSelfIntroduction: String?
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
            profileImage = profileImage?.let {
                FormDataUtil.getImageBody(
                    profileImage, "profileImage", "profileImage"
                )
            },
            userSelfIntroduction = FormDataUtil.getTextBody(
                "userSelfIntroduction", userSelfIntroduction ?: ""
            )
        )
    }

    suspend fun patchProfile(
        department: String? = null,
        nickname: String? = null,
        profileImage: Bitmap?,
        userSelfIntroduction: String? = null
    ) = service.patchProfile(department = department?.let {
        FormDataUtil.getTextBody(
            "department", it
        )
    },
        profileImage = profileImage?.let {
            FormDataUtil.getImageBody(
                profileImage, "profileImage", "profileImage"
            )
        },
        nickname = nickname?.let { FormDataUtil.getTextBody("nickname", it) },
        userSelfIntroduction = userSelfIntroduction?.let {
            FormDataUtil.getTextBody(
                "userSelfIntroduction", it
            )
        })

    suspend fun getSearchDepartment(name: String) = service.getSearchDepartment(name)

    suspend fun postLogin(loginRequest: LoginRequest) = service.postLogin(loginRequest)

    suspend fun fetchRecentMyInfo() {
        val myInfo = service.getMyInfo()
        dao.updateMyInfo(myInfo.result)
    }

    suspend fun deleteUser(myInfo: MyInfoResult) {
        dao.deleteMyInfo(myInfo)
    }

    val myInfoFlow: Flow<MyInfoResult>
        get() = dao.getMyInfo().flowOn(Dispatchers.Default).conflate()

    suspend fun postReIssueAccessToken(request: RefreshTokenRequest) =
        service.postReIssueAccessToken(request)

    suspend fun postSaveFCMToken(request: SaveFCMTokenRequest) = service.postSaveFCMToken(request)

    suspend fun postLogout(request: LogoutRequest) = service.postLogout(request)

    suspend fun deleteWithdrawal() = service.deleteWithdrawal()

    suspend fun postEmailVerify(request: EmailVerifyRequest) = service.postEmailVerify(request)

    suspend fun patchPassword(request: PasswordRequest) = service.patchPassword(request)

    suspend fun postUserReport(reportRequest: UserReportRequest) = service.postUserReport(reportRequest)
}