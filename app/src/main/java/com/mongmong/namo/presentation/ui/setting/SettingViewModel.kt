package com.mongmong.namo.presentation.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.ProfileModel
import com.mongmong.namo.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {
    private val _profile = MutableLiveData<ProfileModel>()
    val profile: LiveData<ProfileModel> = _profile

    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {
            _profile.value = repository.getProfile()
            Log.d("SettingVM", "profile: ${_profile.value}")
        }
    }
}