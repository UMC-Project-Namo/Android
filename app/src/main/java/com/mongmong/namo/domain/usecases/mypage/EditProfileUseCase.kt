package com.mongmong.namo.domain.usecases.mypage

import android.net.Uri
import com.mongmong.namo.domain.model.BaseResponse
import com.mongmong.namo.domain.model.RegisterInfo
import com.mongmong.namo.domain.repositories.AuthRepository
import com.mongmong.namo.domain.usecases.image.UploadImageToS3UseCase
import javax.inject.Inject

class EditProfileUseCase @Inject constructor(
    private val uploadImageToS3UseCase: UploadImageToS3UseCase
) {
    suspend operator fun invoke(
        profileImage: String,
        nickname: String,
        colorId: Int,
        birthday: String,
        intro: String,
        isBirthdayPublic: Boolean,
        isNamePublic: Boolean
    ): BaseResponse {
        val newImageUrl = uploadImageToS3UseCase.execute(PREFIX, listOf<Uri>(Uri.parse(profileImage)))
        /*
        return authRepository.postSignupComplete(
            RegisterInfo(
                name = name,
                nickname = nickname,
                colorId = colorId,
                birthday = birthday,
                intro = intro,
                profileImage = newImageUrl[0]
            )
        )
        */
        return BaseResponse()
    }

    companion object {
        const val PREFIX = "profile"
    }
}