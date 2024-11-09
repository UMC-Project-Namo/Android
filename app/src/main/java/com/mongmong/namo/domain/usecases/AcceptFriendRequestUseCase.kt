package com.mongmong.namo.domain.usecases

import com.mongmong.namo.data.dto.FriendBaseResponse
import com.mongmong.namo.domain.repositories.FriendRepository
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(private val friendRepository: FriendRepository) {
    suspend fun execute(
        friendRequestId: Long
    ): FriendBaseResponse {
        return friendRepository.acceptFriendRequest(friendRequestId)
    }
}