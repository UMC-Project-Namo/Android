package com.mongmong.namo.data.dto

import com.mongmong.namo.presentation.config.BaseResponse

data class FriendBaseResponse(
    val result: String = ""
): BaseResponse()

/** 친구 목록 조회 */
data class GetFriendListResponse(
    val result: GetFriendListResult
)

data class GetFriendListResult(
    var friendList: List<FriendList> = emptyList(),
    var totalPage: Int = 0,
    var currentPage: Int = 0,
    var pageSize: Int = 0,
    var totalItems: Int = 0
)

data class FriendList(
    var memberId: Long = 0L,
    var nickname: String = "",
    var favoriteFriend: Boolean = false,
    var profileImage: String? = "",
    var tag: String = "",
    var bio: String = "",
    var birthDay: String = "",
    var favoriteColorId: Int = 0
)


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