package com.mongmong.namo.domain.repositories

import com.mongmong.namo.data.dto.FriendBaseResponse
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.model.FriendRequest

interface FriendRepository {
    suspend fun getFiendList(): List<Friend>

    suspend fun getFriendRequests(): List<FriendRequest>

    suspend fun doFriendRequest(
        nicknameTag: String
    ): FriendBaseResponse

    suspend fun acceptFriendRequest(
        requestId: Long
    ): FriendBaseResponse

    suspend fun rejectFriendRequest(
        requestId: Long
    ): FriendBaseResponse
}