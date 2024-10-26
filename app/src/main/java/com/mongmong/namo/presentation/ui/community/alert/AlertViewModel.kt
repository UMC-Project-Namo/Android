package com.mongmong.namo.presentation.ui.community.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.model.Moim
import com.mongmong.namo.domain.model.group.GroupMember
import org.joda.time.LocalDateTime

class AlertViewModel: ViewModel() {
    private val _moimRequestList = MutableLiveData<List<Moim>>(emptyList())
    val moimRequestList: LiveData<List<Moim>> = _moimRequestList

    private val _friendRequestList = MutableLiveData<List<Friend>>(emptyList())
    val friendRequestList: LiveData<List<Friend>> = _friendRequestList

    init {
        _moimRequestList.value = listOf(
            Moim(
                1, LocalDateTime.now(), "", "나모 모임 일정", "강남역",
                listOf(GroupMember(3, "코코아", 4)
            ))
        )

        _friendRequestList.value = emptyList()
    }
}