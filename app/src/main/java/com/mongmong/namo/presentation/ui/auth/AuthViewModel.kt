package com.mongmong.namo.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.LoginBody
import com.mongmong.namo.domain.model.LoginResult
import com.mongmong.namo.domain.model.RefreshResponse
import com.mongmong.namo.domain.model.TokenBody
import com.mongmong.namo.domain.repositories.AuthRepository
import com.mongmong.namo.presentation.enums.LoginPlatform
import com.mongmong.namo.presentation.config.ApplicationClass.Companion.dsManager
import com.mongmong.namo.presentation.config.Constants.SUCCESS_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    private val _isLogoutComplete = MutableLiveData<Boolean>()
    val isLogoutComplete: LiveData<Boolean> = _isLogoutComplete

    private val _isQuitComplete = MutableLiveData<Boolean>()
    val isQuitComplete: LiveData<Boolean> = _isQuitComplete

    private val _refreshResponse = MutableLiveData<RefreshResponse>()
    val refreshResponse: LiveData<RefreshResponse> = _refreshResponse

    /** 로그인 */
    fun tryLogin(platform: LoginPlatform, accessToken: String, refreshToken: String, userName: String) {
        viewModelScope.launch {
            val response = repository.postLogin(platform.platformName, LoginBody(accessToken, refreshToken))

            if (response.code != SUCCESS_CODE) return@launch
            saveToken(response.result)
            saveLoginPlatform(platform)
            saveUserId(response.result.userId)

            // 로그인 결과 설정
            _loginResult.value = response.result.apply {
                this.userName = userName // 사용자 이름을 추가
            }
        }
    }

    /** 토큰 재발급 */
    fun tryRefreshToken() {
        viewModelScope.launch {
            _refreshResponse.postValue(repository.postTokenRefresh())
        }
    }

    /** 로그아웃 */
    fun tryLogout() {
        viewModelScope.launch {
            if (repository.postLogout()) {
                _isLogoutComplete.value = true
                deleteToken()
            }
        }
    }

    /** 회원탈퇴 */
    fun tryQuit() {
        viewModelScope.launch {
            val isSuccess = repository.postQuit(getLoginPlatform())
            if (isSuccess) {
                _isQuitComplete.value = true
                deleteToken()
            }
        }
    }

    // 약관 동의 여부 확인
    fun checkUpdatedTerms(): Boolean {
        for (term in _loginResult.value!!.terms) {
            if (!term.check) return true
        }
        return false
    }

    /** 토큰 */
    private fun getSavedToken(): TokenBody = runBlocking {
        val accessToken = dsManager.getAccessToken().first().orEmpty()
        val refreshToken = dsManager.getRefreshToken().first().orEmpty()
        return@runBlocking TokenBody(accessToken, refreshToken)
    }

    private fun getLoginPlatform(): String = runBlocking {
        dsManager.getPlatform().first().orEmpty()
    }

    private suspend fun saveLoginPlatform(platform: LoginPlatform) {
        dsManager.savePlatform(platform.platformName)
    }

    private suspend fun saveToken(tokenResult: LoginResult) {
        dsManager.saveAccessToken(tokenResult.accessToken)
        dsManager.saveRefreshToken(tokenResult.refreshToken)
    }

    private suspend fun saveUserId(userId: Long) {
        dsManager.saveUserId(userId)
    }

    private suspend fun deleteToken() {
        dsManager.clearTokens()
    }
}
