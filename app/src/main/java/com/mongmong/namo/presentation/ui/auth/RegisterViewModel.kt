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
    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private val _profileImage = MutableLiveData<Uri?>(null)
    val profileImage: LiveData<Uri?> = _profileImage

    val nickname = MutableLiveData<String>("")

    private val _birthDate = MutableLiveData<String>("") // YYYY-MM-DD 형식으로 저장
    val birthDate: LiveData<String> = _birthDate

    val intro = MutableLiveData<String>("")

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

    // 모든 필드의 하이라이트 상태를 관리하는 LiveData (하나만 사용)
    val highlightFields = MutableLiveData<Map<String, Boolean>>(mapOf(
        "nickname" to false,
        "birth" to false,
        "color" to false
    ))

    // 회원가입 버튼 활성화 여부
    val isRegisterEnabled = MediatorLiveData<Boolean>().apply {
        addSource(isNicknameValid) { checkFormValidation() }
        addSource(isBirthValid) { checkFormValidation() }
        addSource(color) { checkFormValidation() }
    }

    // 버튼 클릭 시 미입력 필드 강조
    fun enableHighlight() {
        highlightFields.value = mapOf(
            "nickname" to nickname.value.isNullOrEmpty(),
            "birthDate" to birthDate.value.isNullOrEmpty(),
            "color" to (color.value == null)
        )
    }

    // 특정 필드 하이라이트 해제 (입력 시 호출)
    fun clearHighlight(field: String) {
        highlightFields.value = highlightFields.value?.toMutableMap()?.apply {
            this[field] = false
        }
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

    fun setUserName(name: String) { _name.value = name }

    fun setColor(categoryColor: CategoryColor?) { _color.value = categoryColor }

    fun setProfileImage(uri: Uri) { _profileImage.value = uri }

    fun setBirthDate(year: String, month: String, day: String) { _birthDate.value = "$year/$month/$day" }

    fun getFormattedBirthDate(): String {
        return _birthDate.value?.replace("-", "/") ?: "생년월일을 선택하세요"
    }

    fun requestRegister() {
        if (isRegisterEnabled.value == true) {
            viewModelScope.launch {
                repository.postSignupComplete(
                    name = name.value ?: "",
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
