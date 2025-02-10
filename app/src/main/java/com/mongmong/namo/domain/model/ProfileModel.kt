package com.mongmong.namo.domain.model

import com.mongmong.namo.presentation.enums.CategoryColor

data class ProfileModel(
    val profileUrl: String?,
    val nickname: String,
    val tag: String,
    val introduction: String,
    val name: String,
    val birth: String,
    val favoriteColor: CategoryColor,
    val nameVisible: Boolean,
    val birthdayVisible: Boolean
)
