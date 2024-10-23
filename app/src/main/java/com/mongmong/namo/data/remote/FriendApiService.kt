package com.mongmong.namo.data.remote

import com.mongmong.namo.data.dto.FriendBaseResponse
import com.mongmong.namo.data.dto.GetFriendRequestResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendApiService {
    /** 친구 정보 */

    /** 친구 요청 */
    // 친구 요청 목록 조회
    @GET("friends/requests")
    suspend fun getFriendRequestList() : GetFriendRequestResponse

    // 친구 요청
    @POST("friends/{nickname-tag}")
    suspend fun postFriendRequest(
        @Path("nickname-tag") nicknameTag: String
    ): FriendBaseResponse

    // 친구 요청 수락
    @PATCH("friends/requests/{friendshipId}/accept")
    suspend fun acceptFriendRequest(
        @Path("friendshipId") friendRequestId: Long
    ): FriendBaseResponse

    // 친구 요청 거절
    @PATCH("friends/requests/{friendshipId}/reject")
    suspend fun rejectFriendRequest(
        @Path("friendshipId") friendRequestId: Long
    ): FriendBaseResponse
}