package com.mongmong.namo.domain.model

data class ProfileModel(
    val profileUrl: String?,
    val nickname: String,
    val tag: String,
    val introduction: String,
    val name: String,
    val birth: String,
    val favoriteColorId: Int,
    val nameVisible: Boolean,
    val birthdayVisible: Boolean
)
