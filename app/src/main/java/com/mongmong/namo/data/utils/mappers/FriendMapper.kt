package com.mongmong.namo.data.utils.mappers

import com.mongmong.namo.data.dto.GetFriendRequestResult
import com.mongmong.namo.domain.model.FriendRequest

object FriendMapper {
    // DTO -> Model
    fun GetFriendRequestResult.toModel(): FriendRequest {
        return FriendRequest(
            userId = this.memberId,
            nicknameTag = this.nickname + this.tag,
            introduction = this.bio,
            birth = this.birth,
            favoriteColorId = this.favoriteColorId
        )
    }
}