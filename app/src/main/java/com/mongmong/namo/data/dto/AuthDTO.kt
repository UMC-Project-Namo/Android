package com.mongmong.namo.data.dto

import com.mongmong.namo.domain.model.BaseResponse

data class SignupCompleteRequest(
    val bio: String,
    val birthday: String,
    val colorId: Int,
    val name: String,
    val nickname: String,
    val profileImage: String
)

data class SignupCompleteResponse(
    val result: SignupCompleteResult
): BaseResponse()

data class SignupCompleteResult(
    val bio: String,
    val birthday: String,
    val colorId: Int,
    val name: String,
    val nickname: String,
    val tag: String,
    val userId: Int
)