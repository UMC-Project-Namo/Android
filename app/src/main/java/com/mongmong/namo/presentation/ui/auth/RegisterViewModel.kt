package com.mongmong.namo.presentation.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {
    private val _profileImage = MutableLiveData<String>()
    val profileImage: LiveData<String> = _profileImage

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    var name: String = ""

    private val _year = MutableLiveData<String>()
    val year: LiveData<String> = _year

    private val _month = MutableLiveData<String>()
    val month: LiveData<String> = _month

    private val _day = MutableLiveData<String>()
    val day: LiveData<String> = _day

    private val _color = MutableLiveData<Int>()
    val color: LiveData<Int> = _color

    private val _intro = MutableLiveData<String>()
    val intro: LiveData<String> = _intro

    fun setUserName(name: String) { this.name = name }

    fun requestRegister() {
        viewModelScope.launch {
            repository.postSignupComplete(
                name = name,
                nickname = _nickname.value ?: "",
                birthday = getBirthDay(),
                colorId = 0,
                bio = _intro.value ?: "",
                profileImage = _profileImage.value ?: ""
            )
        }
    }

    private fun getBirthDay(): String {
        return _year.value + "-" + _month.value + "-" + _day.value
    }
}