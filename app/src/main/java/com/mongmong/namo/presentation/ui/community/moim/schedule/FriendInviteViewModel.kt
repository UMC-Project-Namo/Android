package com.mongmong.namo.presentation.ui.community.moim.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.usecases.friend.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendInviteViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
): ViewModel() {
    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> = _friendList

    init {
        getFriends()
    }

    /** 친구 목록 조회 */
    private fun getFriends() {
        viewModelScope.launch {
            _friendList.value = getFriendsUseCase.execute()
        }
    }
}