package com.mongmong.namo.presentation.ui.community.moim.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mongmong.namo.domain.model.Friend

class FriendInviteViewModel: ViewModel() {
    //TODO: 임시 데이터
    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> = _friendList

    init {
        _friendList.value = emptyList()
    }
}