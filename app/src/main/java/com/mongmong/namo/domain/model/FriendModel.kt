package com.mongmong.namo.domain.model

import java.io.Serializable

data class Friend(
    val userid: Long,
    val profileUrl: String,
    val nickname: String,
    val tag: String,
    val introduction: String,
    val name: String,
    val birth: String,
    val isFavorite: Boolean // 즐겨찾기 여부
): Serializable

data class FriendRequest(
    var userId: Long = 0L,
    var profileImage: String = "",
    var nicknameTag: String = "", // ex. 코코아#0000
    var introduction: String = "",
    var birth: String = "",
    var favoriteColorId: Int = 0
)