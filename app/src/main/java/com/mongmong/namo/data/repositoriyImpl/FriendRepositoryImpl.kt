package com.mongmong.namo.data.repositoriyImpl

import com.mongmong.namo.data.datasource.friend.RemoteFriendDataSource
import com.mongmong.namo.data.dto.FriendBaseResponse
import com.mongmong.namo.data.utils.mappers.FriendMapper.toModel
import com.mongmong.namo.domain.model.FriendRequest
import com.mongmong.namo.domain.repositories.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val remoteFriendDataSource: RemoteFriendDataSource
): FriendRepository {
    override suspend fun getFriendRequests(): List<FriendRequest> {
        return remoteFriendDataSource.getFriendRequests().result.map { friendRequest ->
            friendRequest.toModel()
        }
    }

    override suspend fun doFriendRequest(nicknameTag: String): FriendBaseResponse {
        return remoteFriendDataSource.postFriendRequest(nicknameTag)
    }

    override suspend fun acceptFriendRequest(requestId: Long): FriendBaseResponse {
        return remoteFriendDataSource.acceptFriendRequest(requestId)
    }

    override suspend fun rejectFriendRequest(requestId: Long): FriendBaseResponse {
        return remoteFriendDataSource.rejectFriendRequest(requestId)
    }
}