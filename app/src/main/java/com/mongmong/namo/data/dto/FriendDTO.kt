package com.mongmong.namo.data.dto

import com.mongmong.namo.presentation.config.BaseResponse

data class FriendBaseResponse(
    val result: String = ""
): BaseResponse()

/** 친구 요청 목록 조회 */
data class GetFriendRequestResponse(
    val result: List<GetFriendRequestResult>
)

data class GetFriendRequestResult(
    var memberId: Long = 0L,
    var profileImage: String = "",
    var nickname: String = "",
    var tag: String = "",
    var bio: String = "",
    var birth: String = "",
    var favoriteColorId: Int = 0
)