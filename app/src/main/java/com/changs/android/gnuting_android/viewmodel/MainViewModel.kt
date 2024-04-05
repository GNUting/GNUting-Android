package com.changs.android.gnuting_android.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.EmailVerifyRequest
import com.changs.android.gnuting_android.data.model.LoginRequest
import com.changs.android.gnuting_android.data.model.LoginResponse
import com.changs.android.gnuting_android.data.model.MailCertificationRequest
import com.changs.android.gnuting_android.data.model.PasswordRequest
import com.changs.android.gnuting_android.data.model.SearchDepartmentResponse
import com.changs.android.gnuting_android.data.model.SignUpResponse
import com.changs.android.gnuting_android.data.repository.UserRepository
import com.changs.android.gnuting_android.util.Constant.X_ACCESS_TOKEN
import com.changs.android.gnuting_android.util.Constant.X_REFRESH_TOKEN
import com.changs.android.gnuting_android.util.Event
import com.changs.android.gnuting_android.util.getErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    // 사용자 정보
    var birthDate: String? = null
    var department: String? = null
    var email: String? = null
    var gender: String? = null
    var name: String? = null
    var nickname: String? = null
    var phoneNumber: String? = null
    var profileImage: Bitmap? = null
    var studentId: String? = null
    var password: String? = null
    var userSelfIntroduction: String? = null

    private val _nickNameCheck = MutableLiveData<Boolean>()

    val nickNameCheck: LiveData<Boolean> get() = _nickNameCheck

    private val _mailCertificationNumber = MutableLiveData<Event<String>>()

    val mailCertificationNumber: LiveData<Event<String>> get() = _mailCertificationNumber

    private val _signUpResponse = MutableLiveData<Event<SignUpResponse>>()

    val signUpResponse: LiveData<Event<SignUpResponse>> get() = _signUpResponse

    private val _searchDepartmentResponse = MutableLiveData<SearchDepartmentResponse>()

    val searchDepartmentResponse: LiveData<SearchDepartmentResponse> get() = _searchDepartmentResponse

    val choiceDepartment = MutableLiveData<String>()

    private val _loginResponse = MutableLiveData<Event<LoginResponse>>()

    val loginResponse: LiveData<Event<LoginResponse>>
        get() = _loginResponse

    private val _emailVerifyResponse = MutableLiveData<Event<DefaultResponse>>()

    val emailVerifyResponse: LiveData<Event<DefaultResponse>>
        get() = _emailVerifyResponse

    private val _passwordResponse = MutableLiveData<Event<DefaultResponse>>()

    val passwordResponse: LiveData<Event<DefaultResponse>>
        get() = _passwordResponse

    private val _toast = MutableLiveData<Event<String?>>()

    val toast: LiveData<Event<String?>>
        get() = _toast


    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    fun getCheckNickName(inputNickname: String?) {
        viewModelScope.launch {
            inputNickname?.let {
                try {
                    _spinner.value = true
                    val result = repository.getCheckNickName(it)
                    if (result.isSuccessful && result.body() != null) {
                        nickname = inputNickname
                        _nickNameCheck.value = result.body()!!.result
                        _toast.value = Event(result.body()!!.message)
                        _spinner.value = false
                    } else {
                        nickname = null
                        _nickNameCheck.value = false
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    nickname = null
                    _nickNameCheck.value = false
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            }
        }
    }

    fun postMailCertification() {
        viewModelScope.launch {
            email?.let {
                try {
                    _spinner.value = true
                    val requestBody = MailCertificationRequest(it)
                    val result = repository.postMailCertification(requestBody)
                    if (result.isSuccessful && result.body() != null) {
                        _mailCertificationNumber.value = Event(result.body()!!.result.number)
                        _spinner.value = false
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            }
        }
    }

    fun postSignUp() {
        viewModelScope.launch {
            if (birthDate != null && email != null && gender != null && nickname != null && password != null && phoneNumber != null && studentId != null) {
                try {
                    _spinner.value = true
                    val result = repository.postSignUp(
                        birthDate = birthDate!!,
                        department = department!!,
                        email = email!!,
                        gender = gender!!,
                        name = name!!,
                        nickname = nickname!!,
                        password = password!!,
                        phoneNumber = phoneNumber!!,
                        studentId = studentId!!,
                        profileImage = profileImage,
                        userSelfIntroduction = userSelfIntroduction
                    )
                    if (result.isSuccessful && result.body() != null) {
                        _signUpResponse.value = Event(result.body()!!)
                        _spinner.value = false

                        val accessToken = result.body()!!.result.accessToken
                        val refreshToken = result.body()!!.result.refreshToken

                        GNUApplication.sharedPreferences.edit()
                            .putString(X_ACCESS_TOKEN, accessToken).apply()
                        GNUApplication.sharedPreferences.edit()
                            .putString(X_REFRESH_TOKEN, refreshToken).apply()
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            } else {
                _toast.value = Event("네트워크 에러가 발생했습니다.")
            }
        }
    }

    fun getSearchDepartment(department: String?) {
        viewModelScope.launch {
            department?.let {
                try {
                    _spinner.value = true
                    val result = repository.getSearchDepartment(it)
                    if (result.isSuccessful && result.body() != null) {
                        _searchDepartmentResponse.value = result.body()
                        _spinner.value = false
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            }
        }
    }

    fun postLogin(email: String?, password: String?) {
        viewModelScope.launch {
            if (email != null && password != null) {
                try {
                    _spinner.value = true
                    val result = repository.postLogin(LoginRequest(email, password))
                    if (result.isSuccessful && result.body() != null) {
                        _loginResponse.value = Event(result.body()!!)
                        _spinner.value = false

                        val accessToken = result.body()!!.result.accessToken
                        val refreshToken = result.body()!!.result.refreshToken

                        GNUApplication.sharedPreferences.edit()
                            .putString(X_ACCESS_TOKEN, accessToken).apply()
                        GNUApplication.sharedPreferences.edit()
                            .putString(X_REFRESH_TOKEN, refreshToken).apply()
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            } else {
                _toast.value = Event("이메일 또는 패스워드 입력이 완료되지 않았습니다.")
            }
        }
    }


    fun postEmailVerify(number: String) {
        viewModelScope.launch {
            if (email != null) {
                try {
                    _spinner.value = true
                    val result = repository.postEmailVerify(EmailVerifyRequest(email!!, number))
                    if (result.isSuccessful && result.body() != null) {
                        _emailVerifyResponse.value = Event(result.body()!!)
                        _spinner.value = false
                        _toast.value = Event(result.body()!!.result)
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            } else {
                _toast.value = Event("이메일 입력이 완료되지 않았습니다.")
            }
        }
    }

    fun patchPassword() {
        viewModelScope.launch {
            if (email != null && password != null) {
                try {
                    _spinner.value = true
                    val result = repository.patchPassword(PasswordRequest(email!!, password!!))
                    if (result.isSuccessful && result.body() != null) {
                        _spinner.value = false
                        _toast.value = Event(result.body()!!.result)
                        _passwordResponse.value = Event(result.body()!!)
                    } else {
                        result.errorBody()?.let {
                            val errorBody = getErrorResponse(it)
                            errorBody?.let { error ->
                                _spinner.value = false
                                _toast.value = Event(error.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    _spinner.value = false
                    _toast.value = Event("네트워크 에러가 발생했습니다.")
                }
            } else {
                _toast.value = Event("입력이 완료되지 않았습니다.")
            }
        }
    }

}