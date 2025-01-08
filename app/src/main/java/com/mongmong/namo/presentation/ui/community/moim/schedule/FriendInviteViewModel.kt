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
    // 모든 친구 목록
    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> = _friendList

    // 초대할 친구 목록
    private val _friendToInviteList = MutableLiveData<ArrayList<Friend>>(ArrayList())
    val friendToInviteList: LiveData<ArrayList<Friend>> = _friendToInviteList

    init {
        getFriends()
    }

    /** 친구 목록 조회 */
    private fun getFriends() {
        viewModelScope.launch {
            _friendList.value = getFriendsUseCase.execute()
        }
    }

    // 초대할 친구 선택 초기화
    fun resetAllSelectedFriend() {
        _friendToInviteList.value = ArrayList()
    }

    // 친구 초대 상태 변경
    fun updateSelectedFriend(isSelected: Boolean, friend: Friend) {
        val tempFriendArr =  _friendToInviteList.value!!
        if (isSelected) tempFriendArr.add(friend)
        else tempFriendArr.remove(friend)
        _friendToInviteList.value = tempFriendArr
    }
}