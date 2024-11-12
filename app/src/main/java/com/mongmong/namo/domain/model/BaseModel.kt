package com.mongmong.namo.domain.model

data class ActionResponse(
    val result: String = "",
    val code: Int = 0,
    val message: String = "",
    val isSuccess: Boolean = false
)
