package com.mongmong.namo.data.utils.mappers

import com.mongmong.namo.data.dto.FriendList
import com.mongmong.namo.data.dto.GetFriendRequestResult
import com.mongmong.namo.domain.model.Friend
import com.mongmong.namo.domain.model.FriendRequest

object FriendMapper {
    fun FriendList.toModel(): Friend {
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