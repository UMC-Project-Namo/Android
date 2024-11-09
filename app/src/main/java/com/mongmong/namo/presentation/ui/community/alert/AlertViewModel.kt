package com.mongmong.namo.presentation.ui.community.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mongmong.namo.domain.model.FriendRequest
import com.mongmong.namo.domain.model.Moim
import com.mongmong.namo.domain.model.group.GroupMember
import com.mongmong.namo.domain.repositories.FriendRepository
import com.mongmong.namo.domain.usecases.AcceptFriendRequestUseCase
import com.mongmong.namo.domain.usecases.DenyFriendRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val denyFriendRequestUseCase: DenyFriendRequestUseCase
): ViewModel() {
    private val _moimRequestList = MutableLiveData<List<Moim>>(emptyList())
    val moimRequestList: LiveData<List<Moim>> = _moimRequestList

    private val _friendRequestList = MutableLiveData<List<FriendRequest>>(emptyList())
    val friendRequestList: LiveData<List<FriendRequest>> = _friendRequestList

    private val _isComplete = MutableLiveData<Boolean>()
    val isComplete: LiveData<Boolean> = _isComplete

    init {
        _moimRequestList.value = listOf(
            Moim(
                1, LocalDateTime.now(), "", "나모 모임 일정", "강남역",
                listOf(GroupMember(3, "코코아", 4)
            ))
        )
        getFriendRequests()
    }

    /** 친구 요청 목록 조회 */
    fun getFriendRequests() {
        viewModelScope.launch {
            _friendRequestList.value = friendRepository.getFriendRequests()
        }
    }

    /** 친구 요청 수락 */
    fun acceptFriendRequest(requestId: Long) {
        viewModelScope.launch {
            _isComplete.value = acceptFriendRequestUseCase.execute(requestId).isSuccess
        }
    }

    /** 친구 요청 거절 */
    fun denyFriendRequest(requestId: Long) {
        viewModelScope.launch {
            _isComplete.value = denyFriendRequestUseCase.execute(requestId).isSuccess
        }
    }
}