package com.mongmong.namo.presentation.ui.community.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.repositories.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository
): ViewModel() {
    private val _friendList = MutableLiveData<List<Friend>>(emptyList())
    val friendList: LiveData<List<Friend>> = _friendList

    init {
        getFriends()
    }

    /** 친구 목록 조회 */
    fun getFriends() {
        viewModelScope.launch {
            _friendList.value = repository.getFiendList()
        }
    }
}