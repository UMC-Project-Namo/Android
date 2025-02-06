package com.mongmong.namo.presentation.ui.auth

import android.net.Uri
import androidx.lifecycle.*
import com.mongmong.namo.domain.repositories.AuthRepository
import com.mongmong.namo.presentation.enums.CategoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _profileImage = MutableLiveData<Uri?>(null)
    val profileImage: LiveData<Uri?> = _profileImage

    val nickname = MutableLiveData("")
    val birthDate = MutableLiveData("") // YYYY-MM-DD 형식으로 저장
    val intro = MutableLiveData("")

    var name: String = ""

    private val _color = MutableLiveData<CategoryColor?>(null)
    val color: LiveData<CategoryColor?> = _color

    // 닉네임 유효성 검사
    val isNicknameValid: LiveData<Boolean> = Transformations.map(nickname) {
        validateNickname(it)
    }

    // 생년월일 유효성 검사
    val isBirthValid: LiveData<Boolean> = Transformations.map(birthDate) {
        it.isNotEmpty()
    }

    // 자기소개 글자 수 카운트
    val introLength: LiveData<Int> = Transformations.map(intro) { it.length }

    // 회원가입 버튼 활성화 여부
    val isRegisterEnabled = MediatorLiveData<Boolean>().apply {
        addSource(isNicknameValid) { checkFormValidation() }
        addSource(isBirthValid) { checkFormValidation() }
        addSource(color) { checkFormValidation() }
    }

    private fun validateNickname(nickname: String): Boolean {
        val regex = "^[a-zA-Z0-9가-힣]{1,12}$".toRegex()
        return nickname.matches(regex)
    }

    private fun checkFormValidation() {
        isRegisterEnabled.value =
            isNicknameValid.value == true &&
                    isBirthValid.value == true &&
                    color.value != null
    }

    fun setColor(categoryColor: CategoryColor?) {
        _color.value = categoryColor
    }

    fun setProfileImage(uri: Uri) {
        _profileImage.value = uri
    }

    fun setBirthDate(year: String, month: String, day: String) {
        birthDate.value = "$year-$month-$day"
    }

    fun getBirthYear(): String {
        return birthDate.value?.split("-")?.getOrNull(0) ?: ""
    }

    fun getBirthMonth(): String {
        return birthDate.value?.split("-")?.getOrNull(1) ?: ""
    }

    fun getBirthDay(): String {
        return birthDate.value?.split("-")?.getOrNull(2) ?: ""
    }

    fun requestRegister() {
        if (isRegisterEnabled.value == true) {
            viewModelScope.launch {
                repository.postSignupComplete(
                    name = name,
                    nickname = nickname.value ?: "",
                    birthday = birthDate.value ?: "",
                    colorId = color.value?.colorId ?: 0,  // CategoryColor의 colorId 전달
                    bio = intro.value ?: "",
                    profileImage = profileImage.value.toString()
                )
            }
        }
    }
}
