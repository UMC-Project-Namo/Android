package com.mongmong.namo.data.utils.mappers

import com.mongmong.namo.data.dto.FriendDTO
import com.mongmong.namo.data.dto.FriendRequestDTO
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.model.FriendRequest

object FriendMapper {
    fun FriendDTO.toModel(): Friend {
        return Friend(
            userid = this.memberId,
            profileUrl = this.profileImage,
            nickname = this.nickname,
            name = this.nickname,
            introduction = this.bio,
            isFavorite = this.favoriteFriend,
            birth = this.birthDay,
            favoriteColorId = this.favoriteColorId,
            tag = this.tag
        )
    }

    // DTO -> Model
    fun FriendRequestDTO.toModel(): FriendRequest {
        return FriendRequest(
            userId = this.memberId,
            profileUrl = this.profileImage,
            nickname = this.nickname,
            tag = this.tag,
            introduction = this.bio,
            birth = this.birth,
            name = this.nickname,
            favoriteColorId = this.favoriteColorId
        )
    }
}