package com.mongmong.namo.presentation.ui.community.moim.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.repositories.ScheduleRepository
import com.mongmong.namo.domain.usecases.friend.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendInviteViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val getFriendsUseCase: GetFriendsUseCase,
): ViewModel() {
    var moimScheduleId: Long = 0L

    // 모든 친구 목록
    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> = _friendList

    // 초대할 친구 목록
    private val _friendToInviteList = MutableLiveData<ArrayList<Friend>>(ArrayList())
    val friendToInviteList: LiveData<ArrayList<Friend>> = _friendToInviteList

    // API 호출 성공 여부
    private val _isSuccess = MutableLiveData<Boolean>()
    var isSuccess: LiveData<Boolean> = _isSuccess

    init {
        getFriends()
    }

    /** 친구 목록 조회 */
    private fun getFriends() {
        viewModelScope.launch {
            _friendList.value = getFriendsUseCase.execute()
        }
    }

    /** 모임 일정 참석자 초대 */
    fun inviteMoimParticipants() {
        Log.d("FriendInviteVM", "moimScheduleId: $moimScheduleId")
        if (moimScheduleId == 0L) return
        viewModelScope.launch {
            _isSuccess.value = repository.inviteMoimParticipant(moimScheduleId, _friendToInviteList.value!!.map { friend -> friend.userId })
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